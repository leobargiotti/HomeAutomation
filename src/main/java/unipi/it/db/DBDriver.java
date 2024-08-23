package unipi.it.db;

import java.sql.*;
import java.util.HashMap;

public class DBDriver {

    private static final String url = "jdbc:mysql://localhost:3306/HomeAutomationDB?serverTimezone=Europe/Rome";
    private static final String username = "root";
    private static final String password = "ubuntu";

    public static int updateActuators(String address, String actuatorType, Boolean status) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("REPLACE INTO actuators (ip, type, active) VALUES(?,?,?);");
        ps.setString(1, address.substring(1));
        ps.setString(2, actuatorType);
        ps.setBoolean(3, status);
        ps.executeUpdate();
        return ps.getUpdateCount();
    }

    public static HashMap<String, Object> retrieveActuator(String actuatorType) throws SQLException {
        HashMap<String, Object> result = new HashMap<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("SELECT ip, active FROM actuators WHERE type = ?");
        ps.setString(1, actuatorType);
        ResultSet rs = ps.executeQuery();
        if(!rs.next()){
            return result; //empty at this point
        }else {
            result.put("ip", rs.getString("ip"));
            result.put("active", rs.getBoolean("active"));
            rs.close();
            return result;
        }
    }

    public static int insertData(Long value, String type) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO data (value, sensor) VALUES(?,?);");
        ps.setString(1, String.valueOf(value));
        ps.setString(2, type);
        //ps.setString(3, String.valueOf(Instant.now()));
        ps.execute();
        return ps.getUpdateCount();
    }

    public static HashMap<String, Integer> retrieveData() throws SQLException{
        HashMap<String, Integer> result = new HashMap<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("SELECT sensor, value  FROM data WHERE (sensor, timestamp) IN (SELECT sensor, MAX(timestamp) FROM data GROUP BY sensor)");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            result.put(rs.getString("sensor"), rs.getInt("value"));
        }
        rs.close();
        return result;
    }
    public static void resetActuators() throws SQLException{
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("DELETE from actuators");
        ps.executeUpdate();
    }
}
