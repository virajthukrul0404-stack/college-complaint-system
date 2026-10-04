package com.college.complaint;

import com.college.complaint.service.EventBroadcaster;
import com.college.complaint.util.AppConfig;
import com.college.complaint.util.DatabaseInitializer;
import com.college.complaint.util.DbPool;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AppRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        logger.info("========================================================================");
        logger.info(" Starting College Complaint & Feedback System (Tomcat 10 / Jakarta EE)");
        logger.info(" Target Environment: [{}]", AppConfig.getAppEnv());
        logger.info("========================================================================");

        // 1. Validate environment configuration
        try {
            AppConfig.validateStartup();
        } catch (Exception e) {
            logger.error("Configuration validation error: {}", e.getMessage());
            System.exit(1);
        }

        // 2. Initialize Database (idempotent schema & conditional seed)
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            System.exit(1);
        }

        // 3. Port configuration
        int port = AppConfig.getPort();

        // 4. Configure Embedded Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setHostname("0.0.0.0");
        tomcat.setPort(port);
        tomcat.setBaseDir(new File("target/tomcat-embed").getAbsolutePath());

        // Configure HTTP Connector with container-tuned resource bounds
        Connector connector = tomcat.getConnector();
        connector.setProperty("maxThreads", "50");
        connector.setProperty("minSpareThreads", "5");
        connector.setProperty("acceptCount", "20");
        connector.setProperty("connectionTimeout", "20000");

        // 5. Configure RemoteIpValve for Render's reverse proxy
        RemoteIpValve remoteIpValve = new RemoteIpValve();
        remoteIpValve.setRemoteIpHeader("x-forwarded-for");
        remoteIpValve.setProtocolHeader("x-forwarded-proto");
        remoteIpValve.setProtocolHeaderHttpsValue("https");
        remoteIpValve.setPortHeader("x-forwarded-port");
        remoteIpValve.setInternalProxies(".*");
        tomcat.getHost().getPipeline().addValve(remoteIpValve);

        String webappDirLocation = "src/main/webapp";
        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists()) {
            webappDir = new File("src/main/webapp");
        }

        Context ctx = tomcat.addWebapp("", webappDir.getAbsolutePath());
        ctx.setReloadable(false); // Disable file polling reloads in production for CPU savings

        // Session Cookie Security
        ctx.setUseHttpOnly(true);

        // Map target/classes to /WEB-INF/classes so servlets and compiled classes resolve
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        // 6. Graceful JVM shutdown hook (SIGTERM handling)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Graceful shutdown signal received. Releasing resources...");
            try {
                EventBroadcaster.shutdown();
            } catch (Exception ignored) {}
            try {
                DbPool.shutdown();
            } catch (Exception ignored) {}
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (Exception ignored) {}
            logger.info("Shutdown complete.");
        }, "Shutdown-Hook"));

        tomcat.start();
        long readyTime = System.currentTimeMillis() - startTime;
        logger.info("========================================================================");
        logger.info(">>> Application ready in {} ms at http://0.0.0.0:{}/", readyTime, port);
        logger.info(">>> Health check endpoint available at /healthz");
        logger.info("========================================================================");

        tomcat.getServer().await();
    }
}
