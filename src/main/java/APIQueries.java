import com.fasterxml.jackson.databind.ObjectMapper;
import model.Artifacts;
import model.VulnerabilitiesResult;
import model.report.ReportDependencies;
import model.report.ReportModel;
import model.report.ReportVulnerabilities;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.gradle.api.logging.Logger;
import threadPool.FinalThreadWork;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class APIQueries {
    private static final String API_URL = "http://localhost:8080/gradle/dependency/vulnerabilities";
    private static final String API_REPORT_URL = "http://localhost:8080/report";
    //private static final String API_URL = "http://35.234.147.77/gradle/dependency/vulnerabilities";
    //private static final String API_REPORT_URL = "http://35.234.147.77/report";
    private static CloseableHttpClient httpClient;

    /**
     * Initializes the Client to be used in the execution of the program
     */
    public static void startClient(){
        httpClient = HttpClients.createDefault();
    }

    /**
     * Sends a request to the API querying about all vulnerabilities of every dependency in the project.
     * <br>
     * The results are stored in the object representation of the report.
     * @param artifacts   The list of artifacts that represent the dependencies.
     * @param reportModel   Th
     * @param threadWork    The reference to thread where the addition of the vulnerabilities to their dependencies in
     *                      the report model is done.
     * @param logger    A reference to the plugin logger.
     */
    public static void requestDependenciesVulnerabilities(List<Artifacts> artifacts, ReportModel reportModel, FinalThreadWork threadWork, Logger logger) {
        CloseableHttpResponse response = null;
        try {
            logger.info("Request body {}", artifacts.toString());

            ObjectMapper mapper = new ObjectMapper();
            String obj = mapper.writeValueAsString(artifacts);

            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setEntity(new StringEntity(obj));
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Cache-Control", "max-age=500");

            logger.info("Object to write {}", obj);

            response = httpClient.execute(httpPost);

            logger.info("Response Status {}", response.getStatusLine().getStatusCode());

            logger.info("Response {}\n", response.getStatusLine());

            VulnerabilitiesResult[] vulnerabilities = mapper.readValue(response.getEntity().getContent(), VulnerabilitiesResult[].class);

            logger.info("Vulnerabilities {}", (Object[]) vulnerabilities);

            threadWork.addAction(() -> {
                logger.info("Running adding vulnerabilities.");
                for (VulnerabilitiesResult vulnerability : vulnerabilities) {
                    logger.info("Entity {}\n", vulnerability);
                    ReportDependencies dependencies = new ReportDependencies(vulnerability.getTitle(), vulnerability.getMainVersion(), false);

                    logger.info("Report Dependency {}\n", dependencies);
                    List<ReportDependencies> reportDependencies = reportModel.getDependencies()
                            .stream()
                            .filter(dependency -> dependency.equals(dependencies))
                            .collect(Collectors.toList());

                    if (!reportDependencies.isEmpty()) {
                        logger.info("Added vulnerabilities to {}", reportDependencies.get(0));
                        reportDependencies.get(0).setVulnerabilities(vulnerability.getVulnerabilities().toArray(new ReportVulnerabilities[0]));
                        reportDependencies.get(0).setVulnerabilitiesCount(vulnerability.getTotalVulnerabilities());
                    }
                }
            });
        } catch (IOException e) {
            logger.warn("An IOException occurred when trying to get the dependencies vulnerabilities {}.", e.getMessage());
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                logger.warn("The CloseableHttpResponse could not be closed {].", e.getMessage());
            }
        }
    }

    /**
     * Sends the report produced by the plugin to the API.
     * @param reportModel   The report model where the license found will be added.
     * @param logger    A reference to the plugin logger.
     */
    public static void sendReport(ReportModel reportModel, Logger logger) {
        CloseableHttpResponse response = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String report = mapper.writeValueAsString(reportModel);

            HttpPost httpPost = new HttpPost(API_REPORT_URL);
            httpPost.setEntity(new StringEntity(report));
            httpPost.addHeader("Content-Type", "application/json");

            logger.info("Object to write {}", report);

            response = httpClient.execute(httpPost);

            logger.info("Response Status {}", response.getStatusLine().getStatusCode());

            logger.info("Response {}", response.getStatusLine());
        } catch (IOException e) {
            logger.warn("An IOException occurred when trying to send the report produced to the API {}.", e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.warn("The CloseableHttpResponse could not be closed {}.", e.getMessage());
                }
            }
        }
    }

    /**
     * Shutdowns the client used in the course of the programme
     */
    public static void finishClient(Logger logger){
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.warn("The CloseableHttpClient could not be closed {}.", e.getMessage());
        }
    }
}
