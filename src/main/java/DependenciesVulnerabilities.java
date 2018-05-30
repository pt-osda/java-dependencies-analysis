import model.Artifacts;
import model.GradleArtifact;
import model.report.ReportDependencies;
import model.report.ReportModel;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.logging.Logger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DependenciesVulnerabilities {
    private static final List<Artifacts> REQUEST_BODY = new LinkedList<>();
    private static final HashMap<Artifacts, Artifacts> dependenciesPackages = new HashMap<>();
    private static Logger logger;
    private static ReportModel reportModel;

    /**
     * Prepares the request for the API about all the project dependencies, including the direct and indirect ones.
     * @param configurationContainer
     * @param model
     * @param log
     * @return A pair containing the altered report object as the first element and the list of artifacts to query
     */
    protected static ReportModel getVulnerabilities(ConfigurationContainer configurationContainer, ReportModel model, Logger log) {
        logger = log;
        reportModel = model;

        Set<GradleArtifact> gradleArtifacts = configurationContainer
                .stream()
                .flatMap(configuration -> configuration.getResolvedConfiguration().getFirstLevelModuleDependencies().stream())
                .distinct()
                .map(resolvedDependency -> new GradleArtifact(null, resolvedDependency))
                .collect(Collectors.toSet());

        requestChild(null, gradleArtifacts);

        return APIQueries.requestDependenciesVulnerabilities(REQUEST_BODY, reportModel, logger);
    }


    /**
     * Recursive method to add the dependency to query to the API. This finds all the indirect dependencies and adds
     * them to their "parent".
     * @param currentArtifact The dependency that will have their dependency analysed
     * @param children The dependencies of the currentArtifact, which is already a dependency
     */
    private static void requestChild(Artifacts currentArtifact, Set<GradleArtifact> children) {
        children.forEach(gradleArtifact -> {
            Artifacts artifact = new Artifacts(gradleArtifact.getName(), gradleArtifact.getVersion(), gradleArtifact.getGroup());
            logger.info("Artifact {}", currentArtifact);

            List<ReportDependencies> reportDependencies = reportModel.getDependencies()
                    .stream()
                    .filter(dependency -> dependency.getTitle().equals(gradleArtifact.getGroup() + ":" + gradleArtifact.getName()))
                    .collect(Collectors.toList());

            if (!dependenciesPackages.containsKey(artifact)){
                logger.info("Gradle Artifact Child group {}, name {}, version {}", gradleArtifact.getGroup(), gradleArtifact.getName(), gradleArtifact.getVersion());
                REQUEST_BODY.add(artifact);
                dependenciesPackages.put(artifact, currentArtifact);

                // TODO modify to show dependency tree
                ReportDependencies reportDependency = new ReportDependencies(gradleArtifact.getGroup() + ":" + gradleArtifact.getName(), gradleArtifact.getVersion());

                if (!reportModel.getDependencies().contains(reportDependency))
                    reportModel.getDependencies().add(reportDependency);

                requestChild(artifact, gradleArtifact.getChildren());
            }
        });
    }
}