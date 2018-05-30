import model.report.ReportDependencies;
import model.report.ReportModel;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// TODO no model para o ficheiro incluir o groupId e o artifactId e depois nas vulnerabilidades ir buscar informação a esse model
public class ValidateDependenciesTask extends AbstractTask {
    private Logger logger;
    private ReportModel reportModel;

    @TaskAction
    public void validateDependencies(){
        Project project = getProject();
        logger = getLogger();

        reportModel = new ReportModel(project.getName());

        ConfigurationContainer configurationContainer = project.getConfigurations();

        logger.info("There are {} configurations.", configurationContainer.size());

        getDirectDependencies(configurationContainer);

        APIQueries.startClient();

        reportModel = DependenciesVulnerabilities.getVulnerabilities(configurationContainer, reportModel, logger);
        reportModel = DependenciesLicenses.findDependenciesLicenses(configurationContainer, reportModel, logger);

        String thisMoment = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );

        reportModel.setTimestamp(thisMoment);

        APIQueries.sendReport(reportModel, logger);
        APIQueries.finishClient();
    }

    /**
     * Creates in the report object all the direct dependencies in the project.
     * <br>
     * This dependencies are the ones specified in the build.gradle file
     * @param configurationContainer
     */
    private void getDirectDependencies(ConfigurationContainer configurationContainer) {
        List<ReportDependencies> foundDependencies = new LinkedList<>();

        configurationContainer
                .forEach(configuration -> {
                    logger.info("Configuration {}", configuration.getName());
                    configuration.getDependencies()
                            .stream()
                            .filter(dependency -> !foundDependencies.contains(dependency))
                            .map(dependency -> new ReportDependencies(dependency.getGroup() + ":" + dependency.getName(), dependency.getVersion()))
                            .forEach(newDependency -> {
                                foundDependencies.add(newDependency);
                                logger.info("Added dependency {}", newDependency);
                            });
                });
        reportModel.setDependencies(foundDependencies);
    }
}