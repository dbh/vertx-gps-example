package com.davidbharrison.com.vertx.gps;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


/**
 * Created by DHARRISON on 6/23/2016.
 */
public class VertxVerticleMain {

    public static void main(String[] args) throws InterruptedException {
        Logger log = LoggerFactory.getLogger(VertxVerticleMain.class);
        log.info("VertxVerticleMain Starting");

        DeploymentOptions options = new DeploymentOptions();
        JsonObject config = new JsonObject();

        CLI cli = CLI.create("java -jar <gps-verticle.jar>")
                .setSummary("Main starting method")
                .addOption(new Option()
                        .setLongName("conf")
                        .setShortName("c")
                        .setDescription("json config file for main app")
                        .setRequired(true)
                );
        List<String> userCommandLine = Arrays.asList(args);
        CommandLine commandLine = cli.parse(userCommandLine);
        String configFile = commandLine.getOptionValue("c");

        try {
            String content = new String(Files.readAllBytes(Paths.get(configFile)));
            config = new JsonObject(content);

            options.setConfig(config);
            Vertx vertx = Vertx.vertx();

            if (options.getConfig().getBoolean("gps.enabled"))
                vertx.deployVerticle(new GpsTcpClientVerticle(), options);

        }
        catch (IOException ioe) {
            System.out.println("Could not open config file: "+configFile);
        }

        log.info("VertxVerticleMain Ending");
    }
}
