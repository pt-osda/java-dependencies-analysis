import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.CustomLicense;
import model.POMModel;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class MyTask extends AbstractTask {
    private Logger logger;
    private final List<String> VALID_LICENSE_LIST = new LinkedList<>();
    private String pomFile;
    private String licenseFile;
    private final HashMap<String, String> KNOWN_LICENSE = new HashMap<>();
    private boolean invalidLicense = false;

    @TaskAction
    public void validateDependencies(){
        fillLicenses(); // cria o map com as licenças conhecidas.

        Project project = getProject();
        logger = getLogger();

        ConfigurationContainer configurationContainer = project.getConfigurations();

        logger.info("There are {} configurations.", configurationContainer.size());

        for (Configuration configuration : configurationContainer){
            logger.info("Running for configuration {}.", configuration.getName());
            if (!configuration.getName().equals("compile")) continue;   // TODO remove from final form
            Set<File> files = configuration.resolve();

            logger.info("Beginning to get all configuration files");

            for (File currentFile : files) {
                String absoluteFilePath = currentFile.getAbsolutePath();
                logger.info("The current file is {}", absoluteFilePath);

                try {
                    logger.info("Reading jar file");
                    JarFile jarFile = new JarFile(absoluteFilePath);

                    logger.info("JarFile name {}", jarFile.getName());

                    findRequiredFiles(jarFile);

                    getLicenseTechniques(jarFile);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("All configuration files were shown");
        }
    }

    private void fillLicenses() {
        KNOWN_LICENSE.put("Apache Software License, Version 1.1", "Apache Version 1.1");
        KNOWN_LICENSE.put("Apache License, Version 2.0", "Apache Version 2.0");
        KNOWN_LICENSE.put("http://www.apache.org/licenses/LICENSE-2.0", "Apache Version 2.0");
        KNOWN_LICENSE.put("BSD License", "BSD 3-Clause License");  // hamcrest-core1.3 from consul-api
        VALID_LICENSE_LIST.add("Apache Version 2.0");
    }

    private void findRequiredFiles(JarFile jar){
        final Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            logger.info("New entry {}", entry.getName());

            final String entryName = (new File(entry.getName())).getName().toLowerCase(); // the last name of the file or directory
            if (!entry.isDirectory() && entryName.equals("pom.xml")) {
                pomFile = entry.getName();
                logger.info("POM file found: {}", pomFile);
            }
            if (!entry.isDirectory() && entryName.contains("license")){
                licenseFile = entry.getName();
                logger.info("LICENSE File found: {}", licenseFile);
            }
        }
    }

    private void getLicenseTechniques(JarFile jarFile) throws IOException {
        if (pomFile != null){
            logger.info("Going to validate pom file.");
            if(validatePomFile(jarFile)) {
                logger.info("Found license as part of pom.xml file");
                return; // TODO write in file
            } else if(invalidLicense){
                logger.info("License found in pom.xml model is invalid.");
                return;
            }

            if (validateLicenseFromFile(jarFile, pomFile)){
                logger.info("License found as commentary in pom.xml file is valid.");
                return;
            } else if (invalidLicense){
                logger.info("License found as commentary in pom.xml is invalid.");
                return;
            }
        }

        if (licenseFile != null && !invalidLicense){
            logger.info("Going to validate license from pom file commentary.");
            if (validateLicenseFromFile(jarFile, licenseFile)) {
                logger.info("License found in LICENSE file is valid");
            } else if (invalidLicense){
                logger.info("License found in LICENSE file is invalid");
            }
        }
    }

    private boolean validatePomFile(JarFile jar) throws IOException {
        ZipEntry entryPomFile = jar.getEntry(pomFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entryPomFile)));
        XmlMapper xmlMapper = new XmlMapper();
        POMModel pomModel = xmlMapper.readValue(reader, POMModel.class);

        logger.info("Pom Model licenses: {}", pomModel.getLicenses());

        if (!pomModel.getLicenses().isEmpty()){
            for (CustomLicense license : pomModel.getLicenses()) {
                String licenseFound = "";
                if (KNOWN_LICENSE.containsKey(license.getName()))
                    licenseFound = KNOWN_LICENSE.get(license.getName());

                else if (KNOWN_LICENSE.containsKey(license.getUrl()))
                    licenseFound = KNOWN_LICENSE.get(license.getUrl());

                if (!licenseFound.equals("")){
                    if (VALID_LICENSE_LIST.contains(licenseFound))
                        return true;
                }
            }
            invalidLicense = true;
        }
        return false;
    }

    private boolean validateLicenseFromFile(JarFile jarFile, String fileName) throws IOException {
        ZipEntry entryPomFile = jarFile.getEntry(fileName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entryPomFile)));

        String line;
        while ((line = reader.readLine()) != null){
            for (String keysLicense : KNOWN_LICENSE.keySet()) {
                if (line.contains(keysLicense)){
                    logger.info("Line contains key: {}", keysLicense);
                    String license = KNOWN_LICENSE.get(keysLicense);

                    if (VALID_LICENSE_LIST.contains(license))
                        return true;

                    invalidLicense = true;
                    return false;
                }
            }
        }
        return false;
    }
}