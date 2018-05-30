import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.pomRepresentation.CustomLicense;
import model.pomRepresentation.POMModel;
import model.report.ReportDependencies;
import model.report.ReportLicense;
import model.report.ReportModel;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class DependenciesLicenses {
    private static final HashMap<String, String> KNOWN_LICENSE = new HashMap<>();
    private static final List<String> PROCESSED_FILES = new LinkedList<>();

    public static ReportModel findDependenciesLicenses(ConfigurationContainer configurationContainer, ReportModel reportModel, Logger logger){
        fillLicenses();

        for (Configuration configuration : configurationContainer){
            logger.info("Running for configuration {}.", configuration.getName());
            Set<File> files = configuration.resolve();

            logger.info("Beginning to get all configuration files");

            for (File currentFile : files) {
                String absoluteFilePath = currentFile.getAbsolutePath();
                logger.info("The current file is {}", absoluteFilePath);

                try {
                    logger.info("Reading jar file");
                    JarFile jarFile = new JarFile(absoluteFilePath);

                    logger.info("JarFile name {}", jarFile.getName());

                    if (!PROCESSED_FILES.contains(jarFile.getName())) {
                        logger.info("Processing");
                        findRequiredFiles(jarFile, reportModel, logger);
                        PROCESSED_FILES.add(jarFile.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("All configuration files were shown");
        }

        return reportModel;
    }

    /**
     * Finds the pom file if exists and the license file of all dependencies in use
     * @param jar
     * @param reportModel
     * @param logger
     */
    private static void findRequiredFiles(JarFile jar, ReportModel reportModel, Logger logger){
        final Enumeration<JarEntry> entries = jar.entries();

        int files = 0;

        while (entries.hasMoreElements() && files < 2) {
            final JarEntry entry = entries.nextElement();
            logger.info("New entry {}", entry.getName());

            final String entryName = (new File(entry.getName())).getName().toLowerCase(); // the last name of the file or directory
            if (!entry.isDirectory() && entryName.equals("pom.xml")) {
                files++;
                logger.info("POM file found: {}", entry.getName());
                try {
                    validatePomFile(jar, entry.getName(), reportModel, logger );
                    validateLicenseFromFile(jar, entry.getName(), reportModel, logger);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!entry.isDirectory() && entryName.contains("license")){
                files++;
                logger.info("LICENSE File found: {}", entry.getName());
                try {
                    validateLicenseFromFile(jar, entry.getName(), reportModel, logger);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void validatePomFile(JarFile jar, String pomFile, ReportModel reportModel, Logger logger) throws IOException {
        ZipEntry entryPomFile = jar.getEntry(pomFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entryPomFile)));
        XmlMapper xmlMapper = new XmlMapper();
        POMModel pomModel = xmlMapper.readValue(reader, POMModel.class);

        logger.info("Pom Model licenses: {}", pomModel.getLicenses());

        for (CustomLicense license : pomModel.getLicenses()) {
            String licenseFound = "";
            if (KNOWN_LICENSE.containsKey(license.getName()))
                licenseFound = KNOWN_LICENSE.get(license.getName());

            else if (KNOWN_LICENSE.containsKey(license.getUrl()))
                licenseFound = KNOWN_LICENSE.get(license.getUrl());

            if (!licenseFound.equals("")){

                List<ReportDependencies> reportDependency = reportModel.getDependencies()
                        .stream()
                        .filter(reportDependencies -> jar.getName().contains(reportDependencies.getTitle()))
                        .collect(Collectors.toList());

                logger.info("Report Dependency found {}", reportDependency.size());

                ReportLicense reportLicense = new ReportLicense();
                reportLicense.setSpdxId(licenseFound);
                reportLicense.setSource("Tag License from pom file.");

                reportDependency.get(0).addLicense(reportLicense);
            }
        }
    }

    private static void validateLicenseFromFile(JarFile jarFile, String fileName, ReportModel reportModel, Logger logger) throws IOException {
        ZipEntry entryFile = jarFile.getEntry(fileName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entryFile)));

        String line;
        while ((line = reader.readLine()) != null) {
            for (String keysLicense : KNOWN_LICENSE.keySet()) {
                if (line.contains(keysLicense)) {
                    logger.info("Line contains key: {}", keysLicense);
                    String license = KNOWN_LICENSE.get(keysLicense);

                    logger.info("ReportModel dependencies {}", reportModel.getDependencies() == null);
                    logger.info("There are {} dependencies ready to write", reportModel.getDependencies().size());
                    logger.info("Jar file name {}", jarFile.getName());

                    List<ReportDependencies> reportDependency = reportModel.getDependencies()
                            .stream()
                            .filter(dependency -> jarFile.getName().contains(dependency.getTitle().replace(":", "\\")))
                            .collect(Collectors.toList());

                    logger.info("Finish dependencies");
                    logger.info("License {}", license);
                    logger.info("File {}", fileName);

                    ReportLicense reportLicense = new ReportLicense();
                    reportLicense.setSpdxId(license);
                    reportLicense.setSource(String.format("Indicated in %s file", fileName));

                    logger.info("Produced report license");

                    logger.info("Current licenses {}", reportDependency.get(0).getLicenses().toString());

                    logger.info("Writing license number {} to {}", reportDependency.get(0).getLicenses().size(), reportDependency.get(0).getTitle());
                    reportDependency.get(0).addLicense(reportLicense);
                    return;
                }
            }
        }
    }

    /**
     * Prepares all the licenses known to the plugin. This includes all the keywords and links that the dependencies are
     * referred to.
     */
    private static void fillLicenses() {
        KNOWN_LICENSE.put("Apache Software License, Version 1.1", "Apache Version 1.1");
        KNOWN_LICENSE.put("Apache License, Version 2.0", "Apache Version 2.0");
        KNOWN_LICENSE.put("http://www.apache.org/licenses/LICENSE-2.0", "Apache Version 2.0");
        KNOWN_LICENSE.put("BSD License", "BSD 3-Clause License");  // hamcrest-core1.3 from consul-api
        KNOWN_LICENSE.put("BSD 3-clause license", "BSD 3-Clause License");  // antlr4-runtime from kafka-connect-query-language
    }
}
