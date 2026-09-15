package no.cantara.service;

import no.cantara.service.health.HealthResource;
import no.cantara.service.oauth2ping.PingResource;
import no.cantara.simulator.oauth2stubbedserver.OAuth2StubbedServerResource;
import no.cantara.simulator.oauth2stubbedserver.OAuth2StubbedTokenVerifyResource;
import no.cantara.util.Configuration;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.web.context.ContextLoaderListener;

import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-07-09
 */
public class Main {
    public static final String CONTEXT_PATH = "/microservice-baseline";
    public static final String ADMIN_ROLE = "admin";
    public static final String USER_ROLE = "user";

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Integer webappPort;
    private Server server;


    public Main() {
        this.server = new Server();
    }

    public Main withPort(Integer webappPort) {
        this.webappPort = webappPort;
        return this;
    }

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        LogManager.getLogManager().getLogger("").setLevel(Level.INFO);

        Integer webappPort = Configuration.getInt("service.port");

        try {

            final Main main = new Main().withPort(webappPort);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    log.debug("ShutdownHook triggered. Exiting basicauthapplication");
                    main.stop();
                }
            });

            main.start();
            log.debug("Finished waiting for Thread.currentThread().join()");
            main.stop();
        } catch (RuntimeException e) {
            log.error("Error during startup. Shutting down ConfigService.", e);
            System.exit(1);
        }
    }

    // https://github.com/psamsotha/jersey-spring-jetty/blob/master/src/main/java/com/underdog/jersey/spring/jetty/JettyServerMain.java
    public void start() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath(CONTEXT_PATH);


        ConstraintSecurityHandler securityHandler = buildSecurityHandler();
        context.setSecurityHandler(securityHandler);

        ResourceConfig jerseyResourceConfig = new ResourceConfig();
        jerseyResourceConfig.packages("no.cantara");
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(jerseyResourceConfig));
        context.addServlet(jerseyServlet, "/*");

        context.addEventListener(new ContextLoaderListener());

        context.setInitParameter("contextConfigLocation", "classpath:context.xml");

        ServerConnector connector = new ServerConnector(server);
        if (webappPort != null) {
            connector.setPort(webappPort);
        }
        CustomRequestLog requestLog = buildRequestLog();
        server.setRequestLog(requestLog);
        server.addConnector(connector);
        server.setHandler(context);

        try {
            server.start();
        } catch (Exception e) {
            log.error("Error during Jetty startup. Exiting", e);
            // "System. exit(2);"
        }
        webappPort = connector.getLocalPort();
        log.info("microservice-baseline started on http://localhost:{}{}", webappPort, CONTEXT_PATH);
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error("Jetty server thread when join. Pretend everything is OK.", e);
        }
    }

    private CustomRequestLog buildRequestLog() {
        // Jetty 10 removed NCSARequestLog. CustomRequestLog with a
        // RequestLogWriter is the replacement; EXTENDED_NCSA_FORMAT is the same
        // output the old setExtended(true) produced.
        RequestLogWriter writer = new RequestLogWriter("logs/jetty-yyyy_mm_dd.request.log");
        writer.setAppend(true);
        writer.setTimeZone("GMT");

        return new CustomRequestLog(writer, CustomRequestLog.EXTENDED_NCSA_FORMAT);
    }

    private ConstraintSecurityHandler buildSecurityHandler() {
        // Jetty 12: a role-restricted constraint implies authentication.
        Constraint userRoleConstraint = Constraint.from(USER_ROLE, ADMIN_ROLE);

        Constraint adminRoleConstraint = Constraint.from(ADMIN_ROLE);

        ConstraintMapping clientConstraintMapping = new ConstraintMapping();
        clientConstraintMapping.setConstraint(userRoleConstraint);
        clientConstraintMapping.setPathSpec("/client/*");

        ConstraintMapping adminRoleConstraintMapping = new ConstraintMapping();
        adminRoleConstraintMapping.setConstraint(adminRoleConstraint);
        adminRoleConstraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.addConstraintMapping(clientConstraintMapping);
        securityHandler.addConstraintMapping(adminRoleConstraintMapping);

        // Allow healthresource to be accessed without authentication
        ConstraintMapping healthEndpointConstraintMapping = new ConstraintMapping();
        healthEndpointConstraintMapping.setConstraint(Constraint.ALLOWED);
        healthEndpointConstraintMapping.setPathSpec(HealthResource.HEALTH_PATH);
        securityHandler.addConstraintMapping(healthEndpointConstraintMapping);

        // Allow OAuth2StubbedServerResource to be accessed without authentication
        ConstraintMapping oauthserverEndpointConstraintMapping = new ConstraintMapping();
        oauthserverEndpointConstraintMapping.setConstraint(Constraint.ALLOWED);
        oauthserverEndpointConstraintMapping.setPathSpec(OAuth2StubbedServerResource.OAUTH2TOKENSERVER_PATH);
        securityHandler.addConstraintMapping(oauthserverEndpointConstraintMapping);

        // Allow OAuth2StubbedServerResource to be accessed without authentication
        ConstraintMapping pingEndpointConstraintMapping = new ConstraintMapping();
        pingEndpointConstraintMapping.setConstraint(Constraint.ALLOWED);
        pingEndpointConstraintMapping.setPathSpec(PingResource.PING_PATH);
        securityHandler.addConstraintMapping(pingEndpointConstraintMapping);


        // Allow tokenverifyerResource to be accessed without authentication
        ConstraintMapping tokenVerifyConstraintMapping = new ConstraintMapping();
        tokenVerifyConstraintMapping.setConstraint(Constraint.ALLOWED);
        tokenVerifyConstraintMapping.setPathSpec(OAuth2StubbedTokenVerifyResource.OAUTH2TOKENVERIFY_PATH);
        securityHandler.addConstraintMapping(tokenVerifyConstraintMapping);

        HashLoginService loginService = new HashLoginService("microservice-baseline");

        String clientUsername = Configuration.getString("login.user");
        String clientPassword = Configuration.getString("login.password");
        UserStore userStore = new UserStore();
        userStore.addUser(clientUsername, new Password(clientPassword), new String[]{USER_ROLE});

        String adminUsername = Configuration.getString("login.admin.user");
        String adminPassword = Configuration.getString("login.admin.password");
        userStore.addUser(adminUsername, new Password(adminPassword), new String[]{ADMIN_ROLE});
        loginService.setUserStore(userStore);

        log.debug("Main instantiated with basic auth clientuser={} and adminuser={}", clientUsername, adminUsername);
        securityHandler.setLoginService(loginService);
        return securityHandler;
    }


    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            log.warn("Error when stopping Jetty server", e);
        }
    }

    public int getPort() {
        return webappPort;
    }

    public boolean isStarted() {
        return server.isStarted();
    }
}