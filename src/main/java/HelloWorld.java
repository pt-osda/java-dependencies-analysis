import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.logging.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class HelloWorld implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.info(String.format("Hello World my getName is %s", project.getName()));

        //project.getConfigurations().getByName("poms").getArtifacts().getFiles().
        try {
            PrintWriter writer = new PrintWriter("Report.txt");
            BufferedReader reader = new BufferedReader(new FileReader("Prototipo.txt"));

            String input = reader.readLine();
            StringBuilder result = new StringBuilder();

            while(input != null){
                result.append(input);
                result.append(System.lineSeparator());
                input = reader.readLine();
            }
            logger.info("Finish reading file");
            writer.print(result.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ConfigurationContainer configurationContainer = project.getConfigurations();

        Configuration configuration = configurationContainer.getByName("compile");

        //configuration.
        DependencySet dependencySet = configuration.getDependencies();

        for (Dependency dependency : dependencySet) {
            logger.info(String.format("Compile Configuration Dependency %s", dependency.getName()));
            //Project
            //dependency.get
        }
    }
}