import com.fasterxml.jackson.databind.ObjectMapper;
import model.Artifacts;
import model.GradleArtifact;
import model.Policy;
import model.report.ReportDependencies;
import model.report.ReportModel;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import java.io.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ValidateDependenciesTask extends AbstractTask {
    private Logger logger;
    private ReportModel reportModel;

    @TaskAction
    // TODO Add invalid licenses
    // TODO add "token" for API access
    // TODO fail if true in policy and invalid licenses
    // TODO add admin
    // TODO add cache time
    public void validateDependencies(){
        Project project = getProject();
        logger = getLogger();

        Policy policy;

        File policyFile = project.file(".osda");
        InputStream inJson = null;

        // TODO check how to react when there is no policy file.
        try {
            inJson = new FileInputStream(policyFile);
            policy = new ObjectMapper().readValue(inJson, Policy.class);
        } catch (IOException e) {
            logger.error("Exception thrown when trying to read the policy file {}.", e.getMessage());
            return;
        } finally {
            if (inJson != null) {
                try {
                    inJson.close();
                } catch (IOException e) {
                    logger.warn("Exception launched when attempting to close the InputStream {}.", e.getMessage());
                }
            }
        }

        logger.info("Policy data: {}.", policy);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        logger.info("Running with {} processors.", availableProcessors);

        ExecutorService finalExecutor = Executors.newSingleThreadExecutor();
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors * 2);

        reportModel = new ReportModel(policy);

        ConfigurationContainer configurationContainer = project.getConfigurations();

        logger.info("There are {} configurations.", configurationContainer.size());

        getDependencies(configurationContainer);

        logger.info("Report Model {}.", reportModel);
        APIQueries.startClient();

        logger.info("Create threadWork.");

        executor.submit(() ->
                DependenciesVulnerabilities.getVulnerabilities(reportModel, finalExecutor, logger)
        );

        DependenciesLicenses.findDependenciesLicenses(configurationContainer, reportModel, executor, finalExecutor, logger);

        executor.shutdown();
        try {
            while(!executor.awaitTermination(500, TimeUnit.MILLISECONDS))
                logger.info("Waiting for first shutdown"); // TODO check
        } catch (InterruptedException e) {
            logger.warn("An exception occurred while waiting for the shutdown of threadPool {}.", e.getMessage()); // TODO handle
        }

        logger.info("End first shutdown");
        finalExecutor.shutdown();

        try {
            while (!finalExecutor.awaitTermination(500, TimeUnit.MILLISECONDS))
                logger.info("Waiting for second shutdown.");
        } catch (InterruptedException e) {
            logger.warn("An exception occurred while waiting for the shutdown of merge thread (thread responsible for the junction of the elements of reportModel {}.", e.getMessage()); // TODO handle
        }
        
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
                            .map(dependency -> new ReportDependencies(dependency.getGroup() + ":" + dependency.getName(), dependency.getVersion(), true))
                            .filter(dependency -> !foundDependencies.contains(dependency))
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

                String childrenName = gradleArtifact.getGroup() + ":" + gradleArtifact.getName() + ":" + gradleArtifact.getVersion();

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