package unipi.it.app;

import unipi.it.coap.COAPClient;
import unipi.it.db.DBDriver;
import java.sql.SQLException;
import java.util.HashMap;

public class PeriodicDataRetrieval implements Runnable{ //singleton class


    private static HashMap<String, Integer> thresholds = new HashMap<>();
    private static final PeriodicDataRetrieval instance = new PeriodicDataRetrieval();

    private PeriodicDataRetrieval(){
        thresholds.put("temperature", 20); // celsius
        thresholds.put("humidity", 40); // %
        thresholds.put("light", 100); // lux
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
                for(String key : values.keySet()) {
                    HashMap<String, Object> act = DBDriver.retrieveActuator(key);

                    if (!act.isEmpty()) {
                        //System.out.println("act is not empty");
                        if (values.get(key) > thresholds.get(key)) {
                            COAPClient.setSensors(key, true);
                            if (! (boolean) act.get("active")) {
                                System.err.println("Danger detected on " + key + " sensor, advertising the actuator");
                                COAPClient.actuatorCall((String) act.get("ip"), key, true, 1);
                            }
                        } else {
                            COAPClient.setSensors(key, false); //turning off the actuator if under 30% of the threshold
                            if (values.get(key) < thresholds.get(key) * 0.3 && (boolean) act.get("active")) {
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
        thresholds.put("temperature", val);
    }

    public static void setHumidityThreshold(Integer val){
        thresholds.put("humidity", val);
    }

    public static void setLightThreshold(Integer val){
        thresholds.put("light", val);
    }

}
