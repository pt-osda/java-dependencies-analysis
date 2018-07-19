import com.fasterxml.jackson.databind.ObjectMapper;
import model.Artifacts;
import model.Policy;
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
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class APIQueries {
    //private static final String API_URL = "http://localhost:8080/gradle/dependency/vulnerabilities";
    //private static final String API_REPORT_URL = "http://localhost:8080/report";
    private static final String API_URL = "http://35.234.151.254/gradle/dependency/vulnerabilities";
    private static final String API_REPORT_URL = "http://35.234.151.254/report";
    private static CloseableHttpClient httpClient;
    private static String token;

    /**
     * Initializes the Client to be used in the execution of the program
     */
    public static void startClient(){
        httpClient = HttpClients.createDefault();
        token = System.getenv("CENTRAL_SERVER_TOKEN");
    }

    /**
     * Sends a request to the API querying about all vulnerabilities of every dependency in the project.
     * <br>
     * The results are stored in the object representation of the report.
     * @param artifacts   The list of artifacts that represent the dependencies.
     * @param reportModel   The report model where the license found will be added.
     * @param policy  The policy model that indicates how the plugin must operate.
     * @param errorMessage  The list of error message to write the error to.
     * @param finalExecutor The reference to thread where the addition of the vulnerabilities to their dependencies in
     *                      the report model is done.
     * @param logger    A reference to the plugin logger.
     */
    public static void requestDependenciesVulnerabilities(List<Artifacts> artifacts, ReportModel reportModel, Policy policy, List<String> errorMessage, ExecutorService finalExecutor, Logger logger) {
        CloseableHttpResponse response = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String obj = mapper.writeValueAsString(artifacts);

            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setEntity(new StringEntity(obj));
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Cache-Control", "max-age=" + policy.getApiCacheTime());
            httpPost.addHeader("Authorization", "Bearer " + token);

            logger.info("Object to write {}", obj);

            response = httpClient.execute(httpPost);

            logger.info("Response Status {}", response.getStatusLine().getStatusCode());

            logger.info("Response {}\n", response.getStatusLine());

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                VulnerabilitiesResult[] vulnerabilities = mapper.readValue(response.getEntity().getContent(), VulnerabilitiesResult[].class);

                logger.info("Vulnerabilities {}", (Object) vulnerabilities);

                finalExecutor.submit(() -> {
                    if (vulnerabilities.length > 0 && policy.isFail()) {
                        String message = String.format("There were found %s vulnerabilities and the policy indicates that the build process should fail.", vulnerabilities.length);
                        errorMessage.add(message);
                        logger.info("{}\n\n\n", message);

                        reportModel.setErrorInfo(message);
                        reportModel.setSuccessfulBuild(false);
                    }

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
            } else {
                String errorInfo = String.format("The request to the API was not successful. Status code was %s.", statusCode);
                logger.info(errorInfo);
                finalExecutor.submit(() ->
                    reportModel.setErrorInfo(errorInfo)
                );
            }
        } catch (IOException e) {
            String errorInfo = String.format("An IOException occurred when trying to get the dependencies vulnerabilities %s.", e.getMessage());
            logger.warn(errorInfo);
            finalExecutor.submit(() ->
                    reportModel.setErrorInfo(errorInfo)
            );
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
            httpPost.addHeader("Authorization", "Bearer " + token);

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
     * @param logger    A reference to the plugin logger.
     */
    public static void finishClient(Logger logger){
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.warn("The CloseableHttpClient could not be closed {}.", e.getMessage());
        }
    }
}
