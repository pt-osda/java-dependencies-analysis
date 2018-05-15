import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JavaDependenciesAnalysis implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Task validateDependencies = project.getTasks().create("validateDependencies", ValidateDependenciesTask.class);
        validateDependencies.setDescription("Validates the project's dependencies in terms of license and vulnerabilities.");
        System.out.println(String.format("Is task enabled %s", validateDependencies.getEnabled()));
    }
}