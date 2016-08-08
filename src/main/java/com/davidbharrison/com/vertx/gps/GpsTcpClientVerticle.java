package com.davidbharrison.com.vertx.gps;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;


/**
 * Created by dbh on 6/6/16.
 */
public class GpsTcpClientVerticle extends AbstractVerticle {
    public void start() {
        Logger log = LoggerFactory.getLogger(GpsTcpClientVerticle.class);
        log.info("GpsTcpClientVerticle starting");

        final JsonObject config = vertx.getOrCreateContext().config();
        log.info("Options config: " + config);

        NetClient tcpClient = vertx.createNetClient();
        EventBus eb = vertx.eventBus();

        tcpClient.connect(
                config.getInteger("gps.port",2947),
                config.getString("gps.host", "localhost"),
                new Handler<AsyncResult<NetSocket>>(){

            @Override
            public void handle(AsyncResult<NetSocket> result) {
                NetSocket socket = result.result();
                //TODO GPSD Client, see if we can pass command to GPSD to eliminate some noise
                socket.write("?WATCH={\"enable\":true,\"json\":true};\r\n");
                socket.handler(new Handler<Buffer>(){
                    @Override
                    public void handle(Buffer buffer) {
                        String gpsDataString = buffer.getString(0, buffer.length()).trim();
                        log.debug(gpsDataString);

                        // some Garmin NMEA sentences from GPSD are truncated so they are invalid JSON
                        // I only care about the GGA sentence, so I'll check for basic JSON validity
                        // and then filter on GGA tags
                        if (gpsDataString.startsWith("{") && gpsDataString.endsWith("}")) {
                            JsonObject gpsData = new JsonObject(gpsDataString);

                            String tag = gpsData.getString("tag");
                            if (tag!=null && tag.equals("GGA")) {
                                log.debug(gpsData);
                                eb.publish("gps", gpsDataString);
                            }
                        }
                        else {
                            log.warn("Invalid json in gps sentence: "+gpsDataString);
                        }
                    }
                });
            }
        });
        log.info("GpsTcpClientVerticle started");
    }
}
