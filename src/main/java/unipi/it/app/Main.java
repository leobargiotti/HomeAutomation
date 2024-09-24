package unipi.it.app;

import unipi.it.coap.COAPRegistrationHandler;
import unipi.it.mqtt.MQTTHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        ScheduledExecutorService executorService = null;
        try {
            executorService = Executors.newScheduledThreadPool(4);
            executorService.schedule(CommandLineInterface.getInstance(), 30, TimeUnit.SECONDS);
            executorService.schedule(COAPRegistrationHandler.getInstance(), 0, TimeUnit.SECONDS);
            executorService.schedule(MQTTHandler.getInstance(), 0, TimeUnit.SECONDS);
            executorService.scheduleAtFixedRate(PeriodicDataRetrieval.getInstance(), 30, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            executorService.shutdown();
        }

    }

}


