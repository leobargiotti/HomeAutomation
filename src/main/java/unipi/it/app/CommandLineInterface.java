package unipi.it.app;

import unipi.it.coap.COAPClient;
import unipi.it.db.DBDriver;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class CommandLineInterface implements Runnable{

    private static final CommandLineInterface instance = new CommandLineInterface();

    private CommandLineInterface(){
    }

    public static CommandLineInterface getInstance(){
        return instance;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to HomeAutomation system management\n" +
                "The available commands are the following, you can execute them by inserting the corresponding number\n" +
                "1 - Help list\n" +
                "2 - Select a new value for the temperature threshold\n" +
                "3 - Select a new value for the humidity threshold\n" +
                "4 - Select a new value for the light threshold\n" +
                "5 - Change the status of the temperature system\n" +
                "6 - Change the status of the humidity system\n" +
                "7 - Change the status of the light system\n" +
                "8 - Check the actuators status");
        while (true){

            System.out.println("Insert a command: ");
            int input = Integer.parseInt(scanner.nextLine().trim());

            switch (input){

                case 1:
                    helpList();
                    break;

                case 2:
                    changeThreshold("temperature", scanner);
                    break;

                case 3:
                    changeThreshold("humidity", scanner);
                    break;

                case 4:
                    changeThreshold("light", scanner);
                    break;

                case 5:
                    changeStatus("temperature", scanner);
                    break;

                case 6:
                    changeStatus("humidity", scanner);
                    break;

                case 7:
                    changeStatus("light", scanner);
                    break;

                case 8:
                    try {
                        printActuatorsStatus();
                    } catch (SQLException e) {
                        System.out.println("impossible to retrieve actuator status");
                    }
                    break;
            }
        }

    }

    private static void printActuatorsStatus() throws SQLException {
        String[] actuators = new String[]{"humidity", "light", "temperature"};
        for(String key: actuators){
            System.out.println("The " + key + " actuator is active : " + DBDriver.retrieveActuator(key).get("active"));
        }
    }
    private static void changeStatus(String actuator, Scanner s) {
        try{
            System.out.println("Would you like to turn the actuator ON or OFF?: ");
            String input = (s.nextLine().trim());
            boolean inputBoolean = true ? input.equals("ON") : false;
            HashMap<String, Object> act = DBDriver.retrieveActuator(actuator);
            if(Objects.equals(act.get("active"), input)){
                System.out.println("Actuator already in this status");
            }
            else {
                COAPClient.actuatorCall((String) act.get("ip"), actuator, inputBoolean, 0);
            }
        } catch (SQLException e){
            System.out.println("Could not retrieve the actuators");
        }

    }

    private static void changeThreshold(String sensor, Scanner s) {
        System.out.println("Insert the new value: ");
        int input = Integer.parseInt(s.nextLine().trim());
        switch (sensor){
            case "temperature":
                PeriodicDataRetrieval.setTemperatureThreshold(input);
                break;
            case "humidity":
                PeriodicDataRetrieval.setHumidityThreshold(input);
                break;
            case "light":
                PeriodicDataRetrieval.setLightThreshold(input);
                break;
        }
        System.out.println("Value changed");
    }

    private static void helpList(){
        System.out.println(
                "The available commands are the following, you can execute them by inserting the corresponding number\n" +
                        "1 - Help list\n" +
                        "2 - Select a new value for the temperature threshold\n" +
                        "3 - Select a new value for the humidity threshold\n" +
                        "4 - Select a new value for the light threshold\n" +
                        "5 - Change the status of the temperature system\n" +
                        "6 - Change the status of the humidity system\n" +
                        "7 - Change the status of the light system\n"+
                        "8 - Check the actuators status");
    }

}
