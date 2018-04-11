import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
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
    private final String[] VALID_LICENSE = {"MIT"};

    @Override
    public void apply(Project project) { // TODO o que fazer com as dependências que não têm ficheiro de licenças nem ficheiro pom, talvez ir buscar ao repositório mas qual ?. Validar dependência
        logger = project.getLogger();
        logger.info(String.format("Hello World my getName is %s", project.getName()));

        ConfigurationContainer configurationContainer = project.getConfigurations();

        Configuration configuration = configurationContainer.getByName("compile");

        Set<ResolvedArtifact> resolvedArtifact = configuration.getResolvedConfiguration().getResolvedArtifacts();

/*
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
                List<String> licenses = retrieveLicenseFile(jarFile);
                logger.info(String.format("Jar File has finished read with %s results.\n Trying to retrieve License\n", licenses.size()));

                if (licenses.size() != 0) {
                    ZipEntry licenseFile = jarFile.getEntry(licenses.get(0));

                    InputStreamReader inputStreamReader = new InputStreamReader(jarFile.getInputStream(licenseFile));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(licenseFile)));

                    String line = reader.readLine();

                    while (line.isEmpty())
                        line = reader.readLine();
                    logger.info(String.format("First line of license is %s \n", line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("All configuration files were shown");
    }

    private List<String> retrieveLicenseFile(final JarFile jar) throws IOException {
        final List<String> licenseEntries = new ArrayList<>();
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            logger.info(String.format("New entry %s", entry.getName()));
            final String entryName = (new File(entry.getName())).getName().toLowerCase();
            if (!entry.isDirectory() && ("license".equals(entryName) || "license.txt".equals(entryName))) {
                logger.info("LICENSE Entry found: {}", entry.getName());
                //entry.
                licenseEntries.add(entry.getName());
            }
        }
        return licenseEntries;
    }
}