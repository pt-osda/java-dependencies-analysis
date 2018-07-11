import model.Artifacts;
import model.report.ReportDependencies;
import model.report.ReportModel;
import org.gradle.api.logging.Logger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class DependenciesVulnerabilities {
    /**
     * Prepares the request for the API about all the project dependencies, including the direct and indirect ones.
     * <br>
     * Passing to the requestDependenciesVulnerabilities method a list of artifacts that is the mapping of the list of
     * reported dependencies in the report model to an Artifact.
     * @param reportModel   The report model where the license found will be added.
     * @param finalExecutor    The reference to the thread doing the mapping of the responses in the final model.
     * @param logger    A reference to the plugin logger.
     */
    public static void getVulnerabilities(ReportModel reportModel, ExecutorService finalExecutor, Logger logger) {
        APIQueries.requestDependenciesVulnerabilities(produceRequestBody(reportModel.getDependencies()), reportModel, finalExecutor, logger);
    }

    /**
     * Converts the reportDependencies in Artifacts so that the API can understand and find vulnerabilities for them.
     * @param reportDependencies    The list of dependencies found in the project.
     * @return  A list of artifacts that represent the dependencies of the project.
     */
    private static List<Artifacts> produceRequestBody(List<ReportDependencies> reportDependencies) {
        return reportDependencies.stream().map(
                reportDependency -> new Artifacts(reportDependency.getArtifactId(), reportDependency.getMainVersion(), reportDependency.getGroup())
        ).collect(Collectors.toList());
    }
}