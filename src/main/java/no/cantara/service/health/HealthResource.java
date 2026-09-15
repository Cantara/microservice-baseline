package no.cantara.service.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

/**
 * Simple health endpoint for checking the server is running
 *
 * @author <a href="mailto:asbjornwillersrud@gmail.com">Asbjørn Willersrud</a> 30/03/2016.
 */
@Path(HealthResource.HEALTH_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {
    public static final String HEALTH_PATH = "/health";
    private static final Logger log = LoggerFactory.getLogger(HealthResource.class);

    @GET
    public Response healthCheck() {
        log.trace("healthCheck");
        String response = String.format("{ \"microservice-health\": \"OK\", \"version\": \"%s\", \"now\":\"%s\", \"running since\": \"%s\"}",
                getVersion(), Instant.now(), getRunningSince());
        return Response.ok(response).build();
    }

    private String getRunningSince() {
        long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString();
    }

    private String getVersion() {
        Properties mavenProperties = new Properties();
        String resourcePath = "/META-INF/maven/no.cantara.service/microservice-baseline/pom.properties";
        URL mavenVersionResource = this.getClass().getResource(resourcePath);
        if (mavenVersionResource != null) {
            try {
                mavenProperties.load(mavenVersionResource.openStream());
                return mavenProperties.getProperty("version", "missing version info in " + resourcePath);
            } catch (IOException e) {
                log.warn("Problem reading version resource from classpath: ", e);
            }
        }
        return "(DEV VERSION)";
    }
}