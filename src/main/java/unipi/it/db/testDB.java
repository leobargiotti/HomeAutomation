package unipi.it.db;


import java.sql.SQLException;

public class testDB {
    public static void main(String[] args) throws SQLException {
        try{
            System.out.println(DBDriver.updateActuators("192.168.0.1", "temperature", true));
            System.out.println(DBDriver.retrieveActuator("temperature"));
            DBDriver.resetActuators();
        }catch (Exception e){
            System.out.println("PROBLEM");
            System.out.println(e);
        }

    }
}