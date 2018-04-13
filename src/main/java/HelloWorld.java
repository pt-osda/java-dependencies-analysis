import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.logging.Logger;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class HelloWorld implements Plugin<Project> {
    private Logger logger;
    private final String[] VALID_LICENSE = {"MIT License"};

    @Override
    public void apply(Project project) { // TODO o que fazer com as dependências que não têm ficheiro de licenças nem ficheiro pom, talvez ir buscar ao repositório mas qual ?. Validar dependência
        logger = project.getLogger();
        logger.info(String.format("Hello World my getName is %s", project.getName()));

        ConfigurationContainer configurationContainer = project.getConfigurations();

        Configuration configuration = configurationContainer.getByName("compile");

       /* Set<ResolvedArtifact> resolvedArtifact = configuration.getResolvedConfiguration().getResolvedArtifacts();


        for (ResolvedArtifact resolvArtif : resolvedArtifact) {
            FileReader reader = null;
            try {
                reader = new FileReader(resolvArtif.getFile());

                char[] line = new char[1024];
                while (reader.read(line) != -1){
                    logger.info(new String(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        Set<File> files = configuration.resolve();

        logger.info("Beginning to get all configuration files");
        for (File currentFile : files) {
            String absoluteFilePath = currentFile.getAbsolutePath();
            logger.info(String.format("The current file is %s", absoluteFilePath));

            try {
                logger.info("Reading jar file");
                JarFile jarFile = new JarFile(absoluteFilePath);
                String licenseFilePath = retrieveLicenseFile(jarFile);

                if (licenseFilePath != null) {
                    ZipEntry licenseFile = jarFile.getEntry(licenseFilePath);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(licenseFile)));

                    String line = reader.readLine();

                    while (line.isEmpty())
                        line = reader.readLine();

                    String newLine = line.trim();

                    logger.info(String.format("First line of license is %s \n", newLine));

                    if (!newLine.equals(VALID_LICENSE[0])){
                        throw new GradleException("License is invalid!!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("All configuration files were shown");
    }

    private String retrieveLicenseFile(final JarFile jar) {
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            logger.info(String.format("New entry %s", entry.getName()));
            final String entryName = (new File(entry.getName())).getName().toLowerCase();
            if (!entry.isDirectory() &&  entryName.contains("license")){
                logger.info("LICENSE Entry found: {}", entry.getName());
                return entry.getName();
            }
        }
        return null;
    }
}