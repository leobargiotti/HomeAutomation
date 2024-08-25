package unipi.it.coap;

import unipi.it.db.DBDriver;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetAddress;
import java.sql.SQLException;

public class COAPRegistrationResource extends CoapResource {


    public COAPRegistrationResource(String name) {
        super(name);
    }

    public void handlePOST (CoapExchange exchange) {
        String s = new String(exchange.getRequestPayload());
        JSONObject obj;
        JSONParser parser = new JSONParser();
        try {
            obj = (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Response response;
        InetAddress address = exchange.getSourceAddress();
        System.out.println("The actuator with ip" + address + " is registering");
        try {
            int modified = DBDriver.updateActuators(address.toString(), (String) obj.get("type"), false); //assuming actuators start in OFF state
            if(modified < 1){
                response = new Response(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            }else{
                response = new Response(CoAP.ResponseCode.CREATED);
            }
        } catch (SQLException e) {
            response = new Response(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            System.err.println("DataBase error: cannot connect");
        }
        exchange.respond(response);
    }

}