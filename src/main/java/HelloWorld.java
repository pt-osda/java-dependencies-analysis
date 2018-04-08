import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HelloWorld implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.info(String.format("Hello World my getName is %s", project.getName()));

        logger.info("Source file");
        try {
            File file = project.getBuildscript().getSourceFile();
            FileReader reader = new FileReader(file);
            char[] line = new char[1024];
            while (reader.read(line) != -1){
                logger.info(new String(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}