import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.POMModel;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class MyTask extends AbstractTask {
    private Logger logger;
    private final String[] VALID_LICENSE = {"MIT License"};

    @TaskAction
    public void pluginAction(){
        Project project = getProject();

        logger = getLogger();
        logger.info("Hello World my getName is {}", getName());

        ConfigurationContainer configurationContainer = project.getConfigurations();

        logger.info("There are {} configurations.", configurationContainer.size());

        for (Configuration configuration : configurationContainer){
            logger.info("Running for configuration {}.", configuration.getName());
            if (!configuration.getName().equals("compile")) continue;
            Set<File> files = configuration.resolve();

            logger.info("Beginning to get all configuration files");

            DependencySet dependencySet = configuration.getDependencies();
            for (File currentFile : files) {
                String absoluteFilePath = currentFile.getAbsolutePath();
                logger.info("The current file is {}", absoluteFilePath);

                try {
                    logger.info("Reading jar file");
                    JarFile jarFile = new JarFile(absoluteFilePath);
                    logger.info("JarFile name {}", jarFile.getName());
                    POMModel pomModel = getPOM(jarFile);

                    if (pomModel != null){
                        logger.info("POM file found. DependencySet contains element {}", dependencySet.contains(pomModel));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("All configuration files were shown");
        }
    }

    private POMModel getPOM(JarFile jar) throws Exception {
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            logger.info("New entry {}", entry.getName());
            final String entryName = (new File(entry.getName())).getName().toLowerCase(); // the last name of the file or directory
            if (!entry.isDirectory() && entryName.equals("pom.xml")) {
                String fileName = entry.getName();
                logger.info("POM file found: {}", fileName);
                ZipEntry pomFile = jar.getEntry(fileName);

                BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(pomFile)));
                XmlMapper xmlMapper = new XmlMapper();
                POMModel pomModel = xmlMapper.readValue(reader, POMModel.class);

                int amount = pomModel.getDevelopers().size();
                int dependencies = pomModel.getDependencies().size();

                logger.info("The dependency was developed by {} people, and contains {} dependencies.", amount, dependencies);
                boolean containsLicense = pomModel.getLicenses().isEmpty();
                logger.info("The dependency does not contains license {}", containsLicense);
                if (!containsLicense) {
                    logger.info("This dependency contains a license. It is {}.", pomModel.getLicenses().get(0).getName());
                } else {
                    logger.info("This dependency does not contain a license.");
                }
                logger.info("Group id {}, artifact id {}, version {}", pomModel.getParent().getGroupId(), pomModel.getParent().getArtifactId(), pomModel.getParent().getVersion());
                return pomModel;
            }
        }
        return null;
    }
}