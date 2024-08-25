package unipi.it.db;


import java.sql.SQLException;

public class testDB {
    public static void main(String[] args) throws SQLException {
        try{
            System.out.println(DBDriver.updateActuators("/192.168.0.1", "temperature", true));
            System.out.println(DBDriver.retrieveActuator("temperature"));
            DBDriver.resetActuators();
        }catch (Exception e){
            throw new RuntimeException();
        }

        try{
            System.out.println(DBDriver.insertData(20L, "temperature"));
            System.out.println(DBDriver.insertData(40L, "humidity"));
            System.out.println(DBDriver.insertData(22L, "temperature"));
            System.out.println(DBDriver.retrieveData());
            DBDriver.resetActuators();
        }catch (Exception e){
            throw new RuntimeException();
        }

    }
}