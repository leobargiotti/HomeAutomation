package unipi.it.app;

import unipi.it.coap.COAPClient;
import unipi.it.db.DBDriver;
import java.sql.SQLException;
import java.util.HashMap;

public class PeriodicDataRetrieval implements Runnable{ //singleton class


    private static HashMap<String, Integer> thresholdsMin = new HashMap<>();
    private static HashMap<String, Integer> thresholdsMax = new HashMap<>();
    private static final PeriodicDataRetrieval instance = new PeriodicDataRetrieval();

    private PeriodicDataRetrieval(){
        thresholdsMin.put("temperature", 18); // celsius
        thresholdsMin.put("humidity", 40); // %
        thresholdsMin.put("light", 100); // lux
        thresholdsMax.put("temperature", 28); // celsius
        thresholdsMax.put("humidity", 60); // %
        thresholdsMax.put("light", 300); // lux
    }

    public static PeriodicDataRetrieval getInstance(){
        return instance;
    }

    public void run() {

        try{
            HashMap<String, Integer> values = DBDriver.retrieveData();
            //System.out.println(values);
            if (values.isEmpty()){
                System.out.println("Could not retrieve any data");
            }
            else{
                boolean checkOff=false;
                for(String key : values.keySet()) {
                    HashMap<String, Object> act = DBDriver.retrieveActuator(key);
                    if (!act.isEmpty()) {
                        //System.out.println("act is not empty");
                        if (values.get(key) < thresholdsMin.get(key) && values.get(key) > thresholdsMax.get(key)) {
                            COAPClient.setSensors(key, true);
                            if (! (boolean) act.get("active")) {
                                checkOff=true;
                                System.err.println("Danger detected on " + key + " sensor, advertising the actuator");
                                COAPClient.actuatorCall((String) act.get("ip"), key, true, 1);
                            }
                        } else {
                            COAPClient.setSensors(key, false); //turning off the actuator if under or upper the thresholds
                            if ((values.get(key) > thresholdsMin.get(key) || values.get(key) < thresholdsMax.get(key))
                                    && (boolean) act.get("active") && checkOff) {
                                checkOff=false;
                                System.err.println("Turning off the " + key + " actuator since there is no danger");
                                COAPClient.actuatorCall((String) act.get("ip"), key, false, 0);
                            }
                        }
                    }
                }
            }
        }catch (SQLException e){
            //System.out.println(e);
            System.err.println("Database error");
        }finally {

            Thread.currentThread().interrupt();

        }
    }

    public static void setTemperatureThreshold(Integer val){
        thresholdsMin.put("temperature", val);
    }

    public static void setHumidityThreshold(Integer val){
        thresholdsMin.put("humidity", val);
    }

    public static void setLightThreshold(Integer val){
        thresholdsMin.put("light", val);
    }

}
