package com.davidbharrison.com.vertx.gps;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by dbh on 8/4/16.
 */
public class GpsEBReceiver extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        EventBus eb = vertx.eventBus();
        eb.consumer("gps", message ->
                System.out.println("Gps EB Recv: "+ message.body())
        );
    }
}
