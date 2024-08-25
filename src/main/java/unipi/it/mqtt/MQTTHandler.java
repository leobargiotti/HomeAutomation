package unipi.it.mqtt;

import unipi.it.db.DBDriver;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;

public class MQTTHandler implements MqttCallback, Runnable  {

    private static final MQTTHandler instance = new MQTTHandler();

    private MQTTHandler(){

    }

    public static MQTTHandler getInstance(){
        return instance;
    }

    LinkedList<String> topic = new LinkedList<>(Arrays.asList("temperature", "humidity", "light"));
    String broker = "tcp://127.0.0.1:1883";
    String clientId = "MQTTHandler";

    public void run() {
        try{
            MqttClient mqttClient = new MqttClient (broker,clientId);
            mqttClient.setCallback(this);
            mqttClient.connect();
            topic.forEach(t -> {
                try {
                    mqttClient.subscribe(t);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (MqttException e){
            System.out.println("Could not connect to the publisher");
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost with the queues");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        JSONObject obj;
        JSONParser parser = new JSONParser();
        try {
            obj = (JSONObject) parser.parse(new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        try{
            if (DBDriver.insertData((Long) obj.get("value"), topic) < 1){   //using Long since JSON parser
                System.err.println("DataBase error: could not insert new data");
            }
        }catch (SQLException e){
            System.err.println("DataBase error: cannot connect");
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

}
