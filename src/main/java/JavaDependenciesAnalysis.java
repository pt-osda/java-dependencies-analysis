import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JavaDependenciesAnalysis implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Task myTask = project.getTasks().create("myTask", MyTask.class);
        System.out.println(String.format("Is task enabled %s", myTask.getEnabled()));
        }
}