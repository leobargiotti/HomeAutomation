package unipi.it.db;


import java.sql.SQLException;

public class testDB {
    public static void main(String[] args) throws SQLException {
        try{
            System.out.println(DBDriver.updateActuators("1", "t", true));
            System.out.println(DBDriver.retrieveActuator("1"));
        }catch (Exception e){
            System.out.println("PROBLEM");
            System.out.println(e);
        }

    }
}