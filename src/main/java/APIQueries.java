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
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class APIQueries {
    private static final String API_URL = "http://localhost:8080/gradle/dependency/vulnerabilities";
    private static final String API_REPORT_URL = "http://localhost:8080/report";
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
     * The results are stored in the object representation of the report
     * @param gradleArtifacts
     */
    public static ReportModel requestDependenciesVulnerabilities(List<Artifacts> gradleArtifacts, ReportModel reportModel, Logger logger) {
        CloseableHttpResponse response = null;
        try {
            logger.info("Request body {}", gradleArtifacts.toString());

            ObjectMapper mapper = new ObjectMapper();
            String obj = mapper.writeValueAsString(gradleArtifacts);

            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setEntity(new StringEntity(obj));
            httpPost.addHeader("Content-Type", "application/json");

            logger.info("Object to write {}", obj);

            response = httpClient.execute(httpPost);

            logger.info("Response Status {}", response.getStatusLine().getStatusCode());

            logger.info("Response {}", response.getStatusLine());

            VulnerabilitiesResult[] vulnerabilities = mapper.readValue(response.getEntity().getContent(), VulnerabilitiesResult[].class);

            for (VulnerabilitiesResult vulnerability : vulnerabilities) {
                logger.info("Entity {}", vulnerability);
                ReportDependencies dependencies = new ReportDependencies(vulnerability.getTitle(), vulnerability.getMainVersion());

                List<ReportDependencies> reportDependencies = reportModel.getDependencies()
                        .stream()
                        .filter(dependency -> dependency.equals(dependencies))
                        .collect(Collectors.toList());

                if (!reportDependencies.isEmpty()){
                    reportDependencies.get(0).setVulnerabilities(vulnerability.getVulnerabilities().toArray(new ReportVulnerabilities[0]));
                    reportDependencies.get(0).setVulnerabilitiesCount(vulnerability.getTotalVulnerabilities());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reportModel; // TODO validar isto
        }
    }

    /**
     * Sends the report produced by the plugin to the API and closes all connections.
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
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Shutdowns the client used in the course of the programme
     */
    public static void finishClient(){
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
