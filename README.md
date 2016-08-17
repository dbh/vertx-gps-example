Vertx-gps-example is an example of how to use Vert.x to read location from a GPS (via GPSD) and write that data onto the Vert.x Event Bus for further processing.  This example code requires a GPSD or gpsdfake from which to receive data.

Here is how to start the GPS verticle

```
vertx run com.davidbharrison.com.vertx.gps.GpsTcpClientVerticle -cp gps-verticle -1.0-SNAPSHOT.jar -conf vertx-gps-conf.json -cluster -cluster-host localhost 
```

The config file vertx-gps-conf.json should contain the following:
```
{
        "gps.port" : 2947,
        "gps.host" : "localhost"
}
```

References: 

* [Vert.x](http://vertx.io/)
* [gpsd](http://catb.org/gpsd/)
* [gpsfake](http://catb.org/gpsd/gpsfake.html)

