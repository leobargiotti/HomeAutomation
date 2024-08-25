package unipi.it.coap;

import org.eclipse.californium.core.CoapServer;
import unipi.it.db.DBDriver;

import java.sql.SQLException;

public class COAPRegistrationHandler extends CoapServer implements Runnable {

    private static final COAPRegistrationHandler instance = new COAPRegistrationHandler();

    private COAPRegistrationHandler(){
    }

    public static COAPRegistrationHandler getInstance(){
        return instance;
    }

    public void run() {
        try {
            DBDriver.resetActuators();
        } catch (SQLException e) {
            System.out.println("impossible to reset actuators table");
        }
        COAPRegistrationHandler server = new COAPRegistrationHandler();
        server.add(new COAPRegistrationResource("registration"));
        server.start();
        System.out.println("Starting the registration server");
    }

}
