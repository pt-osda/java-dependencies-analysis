import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.logging.Logger;
import org.gradle.internal.impldep.org.apache.maven.model.*;
import org.gradle.internal.impldep.org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.gradle.internal.impldep.org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JavaDependenciesAnalysis implements Plugin<Project> {
    public class POMModel {
        private Parent parent;
        private String artifactId;
        private String packaging;
        private String name;
        private String description;
        private List<Dependency> dependencies;
        private List<License> licenses;
        private Scm scm;
        private String licenseFileName;

        public Parent getParent() {
            return parent;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getPackaging() {
            return packaging;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public List<License> getLicenses() {
            return licenses;
        }

        public Scm getScm() {
            return scm;
        }

        public String getLicenseFileName() {
            return licenseFileName;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public void setPackaging(String packaging) {
            this.packaging = packaging;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDependencies(List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        public void setLicenses(List<License> licenses) {
            this.licenses = licenses;
        }

        public void setScm(Scm scm) {
            this.scm = scm;
        }

        public void setLicenseFileName(String licenseFileName) {
            this.licenseFileName = licenseFileName;
        }
    }

    private Logger logger;
    private final String[] VALID_LICENSE = {"MIT License"};

    @Override
    public void apply(Project project) { // TODO o que fazer com as dependências que não têm ficheiro de licenças nem ficheiro pom, talvez ir buscar ao repositório mas qual ?. Validar dependência
        logger = project.getLogger();
        logger.info("Hello World my getName is {}", project.getName());

        ConfigurationContainer configurationContainer = project.getConfigurations();

        Configuration configuration = configurationContainer.getByName("compile");

        Set<File> files = configuration.resolve();

        logger.info("Beginning to get all configuration files");
        for (File currentFile : files) {
            String absoluteFilePath = currentFile.getAbsolutePath();
            logger.info("The current file is {}", absoluteFilePath);

            try {
                logger.info("Reading jar file");
                JarFile jarFile = new JarFile(absoluteFilePath);
                Model pomModel = getPOMModel(jarFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.info("All configuration files were shown");
    }

    private Model getPOMModel(final JarFile jar) throws Exception {
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            logger.info("New entry {}", entry.getName());
            final String entryName = (new File(entry.getName())).getName().toLowerCase(); // the last name of the file or directory
            if (!entry.isDirectory() && entryName.equals("pom.xml")){
                String fileName = entry.getName();
                logger.info("POM file found: {}", fileName);
                ZipEntry pomFile = jar.getEntry(fileName);
                //BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(pomFile)));

                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model = null;
                model = reader.read(new InputStreamReader(jar.getInputStream(pomFile)));

                logger.info("Group id {}, artifact id {}, version {}", model.getParent().getGroupId(), model.getParent().getArtifactId(), model.getParent().getVersion());
                return model;
                //XmlMapper xmlMapper = new XmlMapper();
                //POMModel pomModel = xmlMapper.readValue(reader, POMModel.class);

                //logger.info("Group id {}, artifact id {}, version {}", pomModel.getParent().getGroupId(), pomModel.getParent().getArtifactId(), pomModel.getParent().getVersion());
                //return pomModel;
            }
        }
        return null;
    }
}