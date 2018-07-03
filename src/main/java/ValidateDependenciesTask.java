import model.Artifacts;
import model.GradleArtifact;
import model.report.ReportDependencies;
import model.report.ReportModel;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import threadPool.FinalThreadWork;
import threadPool.ThreadPool;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ValidateDependenciesTask extends AbstractTask {
    private Logger logger;
    private ReportModel reportModel;

    @TaskAction
    public void validateDependencies(){
        Project project = getProject();
        logger = getLogger();

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        logger.info("Running with {} processors.", availableProcessors);

        ThreadPool threadPool = new ThreadPool(availableProcessors, 50, logger);

        reportModel = new ReportModel(project.getName());

        ConfigurationContainer configurationContainer = project.getConfigurations();

        logger.info("There are {} configurations.", configurationContainer.size());

        getDependencies(configurationContainer);

        logger.info("Report Model {}.", reportModel);
        APIQueries.startClient();

        FinalThreadWork threadWork = new FinalThreadWork(reportModel, logger);

        logger.info("Create threadWork.");
        try {
            threadPool.execute(() ->
                DependenciesVulnerabilities.getVulnerabilities(reportModel, threadWork, logger)
            );
        } catch (InterruptedException e) {
            logger.warn("Thread executing getVulnerabilities was interrupted {}.", e.getMessage());
        }

        DependenciesLicenses.findDependenciesLicenses(configurationContainer, reportModel, threadPool, threadWork, logger);

        try {
            threadPool.shutdown();
            threadPool.awaitTermination(1000);
        } catch (InterruptedException e) {
            logger.warn("Await Termination of ThreadPool was interrupted {}.", e.getMessage());
        }

        threadWork.shutdown();
        threadWork.awaitTermination();  // TODO check

        String thisMoment = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        reportModel.setTimestamp(thisMoment);

        logger.info("Finish execution.");
        APIQueries.sendReport(reportModel, logger);
        APIQueries.finishClient(logger);
    }

    /**
     * Adds the project dependencies to the report model for later use in search for their licenses and vulnerabilities.
     * <br>
     * First it will be searched the direct dependencies so that the indirect ones can be added to them.
     * @param configurationContainer   reference to obtain the configurations of the project. That is their artifacts
     *                                 and  dependencies
     */
    private void getDependencies(ConfigurationContainer configurationContainer) {
        getDirectDependencies(configurationContainer);

        getIndirectDependencies(configurationContainer);
    }

    /**
     * Creates in the report object all the direct dependencies in the project.
     * <br>
     * This dependencies are the ones specified in the build.gradle file
     * @param configurationContainer   reference to obtain the configurations of the project. That is their artifacts
     *                                 and  dependencies
     */
    private void getDirectDependencies(ConfigurationContainer configurationContainer) {
        List<ReportDependencies> foundDependencies = new LinkedList<>();

        configurationContainer
                .forEach(configuration -> {
                    logger.info("Configuration {}", configuration.getName());
                    configuration.getDependencies()
                            .forEach(dependency -> logger.info("Dependency {}", dependency));

                    configuration.getDependencies()
                            .stream()
                            .filter(dependency -> !foundDependencies.contains(dependency))
                            .map(dependency -> new ReportDependencies(dependency.getGroup() + ":" + dependency.getName(), dependency.getVersion(), true))
                            .forEach(newDependency -> {
                                foundDependencies.add(newDependency);
                                logger.info("Added dependency {}", newDependency);
                            });
                });

        reportModel.setDependencies(foundDependencies);
    }

    /**
     * Prepares the search for the transitive dependencies of the project, converting the ResolvedDependencies found in
     * the project configuration to the Gradle Artifact representation.
     * @param configurationContainer   reference to obtain the configurations of the project. That is their artifacts
     *                                 and  dependencies
     */
    private void getIndirectDependencies(ConfigurationContainer configurationContainer) {
        Set<GradleArtifact> gradleArtifacts = configurationContainer
                .stream()
                .flatMap(configuration -> configuration.getResolvedConfiguration().getFirstLevelModuleDependencies().stream())
                .distinct()
                .map(resolvedDependency -> new GradleArtifact(null, resolvedDependency))
                .collect(Collectors.toSet());

        requestChild(null, gradleArtifacts);
    }

    /**
     * Search in the gradle artifacts for the indirect dependencies that have to be added to the reports.
     * <br>
     * If the current dependency is indirect of another then it will be added to that dependency children list.
     * <br>
     * If the dependency was not yet created and added to the model then it will be done so. Other wise if the stored
     * dependency did not contain a version then the current dependency version will be added to that dependency in the
     * report model.
     * @param currentArtifact   The current artifact that it is having its children analyzed.
     * @param children  The list of children dependencies of the dependency represented by the currentArtifact
     */
    private void requestChild(Artifacts currentArtifact, Set<GradleArtifact> children) {
        children.forEach(gradleArtifact -> {
            Artifacts artifact = new Artifacts(gradleArtifact.getName(), gradleArtifact.getVersion(), gradleArtifact.getGroup());
            logger.info("Artifact {}\n", artifact);
            logger.info("Current artifact {}", currentArtifact);

            if (currentArtifact != null) {
                ReportDependencies reportDependency = reportModel.getDependencies()
                        .stream()
                        .filter(dependency -> dependency.getTitle().equals(currentArtifact.getGroup() + ":" + currentArtifact.getName()))
                        .collect(Collectors.toList()).get(0);

                String childrenName = gradleArtifact.getGroup() + ":" + gradleArtifact.getName();

                logger.info("Children name {}", childrenName);
                if (!reportDependency.getChildren().contains(childrenName)) {
                    logger.info("Children added to {}", reportDependency.getTitle());
                    reportDependency.addChildren(childrenName);
                }
            }

            ReportDependencies reportDependency = new ReportDependencies(gradleArtifact.getGroup() + ":" + gradleArtifact.getName(), gradleArtifact.getVersion(), false);

            if (!reportModel.getDependencies().contains(reportDependency)) {
                reportModel.getDependencies().add(reportDependency);
                logger.info("Added dependency Indirect {}", reportDependency);
            }
            else {
                ReportDependencies reportDependencies = reportModel.getDependencies().stream().filter(reportDependency::equals).collect(Collectors.toList()).get(0);
                if (reportDependencies.getMainVersion() == null)
                    reportDependencies.setMainVersion(reportDependency.getMainVersion());
            }

            requestChild(artifact, gradleArtifact.getChildren());
        });
    }
}