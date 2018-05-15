import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.*;
import net.ossindex.common.IPackageRequest;
import net.ossindex.common.OssIndexApi;
import net.ossindex.common.PackageDescriptor;
import net.ossindex.common.VulnerabilityDescriptor;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class ValidateDependenciesTask extends AbstractTask {
    private final HashMap<String, String> KNOWN_LICENSE = new HashMap<>();
    private final List<String> VALID_LICENSE_LIST = new LinkedList<>();
    private final HashMap<PackageDescriptor, PackageDescriptor> dependenciesPackages = new HashMap<>();
    private Logger logger;
    private String pomFile;
    private String licenseFile;
    private boolean invalidLicense = false;
    private IPackageRequest request;
    private ReportModel reportModel;

    @TaskAction
    public void validateDependencies(){
        fillLicenses(); // Creates the Map with the known licenses.

        Project project = getProject();
        logger = getLogger();

        reportModel = new ReportModel(project.getName());

        ConfigurationContainer configurationContainer = project.getConfigurations();

        logger.info("There are {} configurations.", configurationContainer.size());

        getAllDependencies(configurationContainer);

        getVulnerabilities(configurationContainer);

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

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File("Report.json"), reportModel);
            } catch (IOException e) {
                logger.info( "An exception occurred. {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates all in the report object all the dependencies in the project.
     * @param configurationContainer
     */
    private void getAllDependencies(ConfigurationContainer configurationContainer) {
        List<ReportDependencies> foundDependencies = new LinkedList<>();

        configurationContainer.forEach(configuration -> {
            logger.info("Configuration {}", configuration.getName());
            configuration.getDependencies().forEach(dependency -> {
                List<ReportDependencies> repeatedDependencies = foundDependencies
                        .stream()
                        .filter(reportDependencies -> reportDependencies.getTitle().equals(dependency.getName()))
                        .distinct()
                        .collect(Collectors.toList());

                logger.info("ReportDependency found {}", repeatedDependencies.size());

                if (repeatedDependencies.isEmpty()) {
                    ReportDependencies newDependency = new ReportDependencies();
                    newDependency.setTitle(dependency.getName());
                    newDependency.setMain_version(dependency.getVersion());
                    foundDependencies.add(newDependency);
                    logger.info("Added dependency {}", newDependency.getTitle());
                }
            });
        });
        reportModel.setDependencies(foundDependencies.toArray(new ReportDependencies[0]));
    }

    private void getVulnerabilities(ConfigurationContainer configurationContainer) {
        Set<GradleArtifact> gradleArtifacts = configurationContainer
                .stream()
                .filter(configuration -> configuration.getName().equals("compile"))
                .flatMap(configuration -> configuration.getResolvedConfiguration().getFirstLevelModuleDependencies().stream())
                .distinct()
                .map(resolvedDependency -> new GradleArtifact(null, resolvedDependency))
                .collect(Collectors.toSet());

        request = OssIndexApi.createPackageRequest();

        requestChild(null, gradleArtifacts);

        try {
            Collection<PackageDescriptor> packageRequests = request.run();
            logger.info("Obtained {} packageDescriptor", packageRequests.size());

            ReportDependencies[] reportDependencies = new ReportDependencies[packageRequests.size()];

            int dependenciesIdx = 0;
            for (PackageDescriptor packageDescriptor : packageRequests) {
                logger.info("Package {} has {} vulnerabilities.", packageDescriptor.getName(), packageDescriptor.getVulnerabilityMatches());
                List<VulnerabilityDescriptor> vulnerabilityDescriptors = packageDescriptor.getVulnerabilities();

                ReportDependencies reportDependency = new ReportDependencies();
                reportDependency.setTitle(packageDescriptor.getName());
                reportDependency.setMain_version(packageDescriptor.getVersion());

                ReportVulnerabilities[] reportVulnerabilities = new ReportVulnerabilities[packageDescriptor.getVulnerabilityMatches()];

                if (packageDescriptor.getVulnerabilityMatches() > 0) {
                    int idx = 0;
                    for (VulnerabilityDescriptor vulnerabilityDescriptor : vulnerabilityDescriptors) {
                        logger.info("Vulnerability id {}, description {}, uriString {}",
                                vulnerabilityDescriptor.getId(),
                                vulnerabilityDescriptor.getDescription(),
                                vulnerabilityDescriptor.getUriString());

                        ReportVulnerabilities reportVulnerability = new ReportVulnerabilities(vulnerabilityDescriptor.getTitle(),
                                vulnerabilityDescriptor.getDescription(),
                                vulnerabilityDescriptor.getReferences().toArray(new String[0]),
                                vulnerabilityDescriptor.getVersions().toArray(new String[0]));

                        reportVulnerabilities[idx++] = reportVulnerability;
                    }
                }
                reportDependency.setVulnerabilities(reportVulnerabilities);
                reportDependencies[dependenciesIdx++] = reportDependency;
                logger.info("Adding dependency {}", reportDependency.getTitle());
            }

            reportModel.setDependencies(reportDependencies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestChild(PackageDescriptor packageDescriptor, Set<GradleArtifact> children) {
        children.forEach(gradleArtifact -> {
             PackageDescriptor descriptor = new PackageDescriptor("maven", gradleArtifact.getGroup(), gradleArtifact.getName(), gradleArtifact.getVersion());
            if (!dependenciesPackages.containsKey(descriptor)){
                logger.info("Gradle Artifact Child group {}, name {}, version {}", gradleArtifact.getGroup(), gradleArtifact.getName(), gradleArtifact.getVersion());
                descriptor = request.add("maven", gradleArtifact.getGroup(), gradleArtifact.getName(), gradleArtifact.getVersion());
                dependenciesPackages.put(descriptor, packageDescriptor);
                requestChild(descriptor, gradleArtifact.getChildren());
            }
        });
    }

    private void fillLicenses() {
        KNOWN_LICENSE.put("Apache Software License, Version 1.1", "Apache Version 1.1");
        KNOWN_LICENSE.put("Apache License, Version 2.0", "Apache Version 2.0");
        KNOWN_LICENSE.put("http://www.apache.org/licenses/LICENSE-2.0", "Apache Version 2.0");
        KNOWN_LICENSE.put("BSD License", "BSD 3-Clause License");  // hamcrest-core1.3 from consul-api
        KNOWN_LICENSE.put("BSD 3-clause license", "BSD 3-Clause License");  // antlr4-runtime from kafka-connect-query-language
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
                    List<ReportDependencies> reportDependency = Arrays.stream(reportModel.getDependencies()).filter(reportDependencies -> jar.getName().contains(reportDependencies.getTitle())).collect(Collectors.toList());
                    logger.info("Report Dependency found {}", reportDependency.size());

                    ReportLicense reportLicense = new ReportLicense();
                    reportLicense.setName(licenseFound);
                    reportLicense.setOrigins("Tag License from pom file.");

                    reportDependency.get(0).setLicense(new ReportLicense[]{reportLicense});

                    if (VALID_LICENSE_LIST.contains(licenseFound))
                        return true;
                }
            }
            invalidLicense = true;
        }
        return false;
    }

    private boolean validateLicenseFromFile(JarFile jarFile, String fileName) throws IOException {
        ZipEntry entryFile = jarFile.getEntry(fileName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entryFile)));

        String line;
        while ((line = reader.readLine()) != null){
            for (String keysLicense : KNOWN_LICENSE.keySet()) {
                if (line.contains(keysLicense)){
                    logger.info("Line contains key: {}", keysLicense);
                    String license = KNOWN_LICENSE.get(keysLicense);

                    logger.info("ReportModel dependencies {}", reportModel.getDependencies() == null);
                    logger.info("There are {} dependencies ready to write", reportModel.getDependencies().length);
                    logger.info("Jar file name {}", jarFile.getName());

                    ReportDependencies reportDependency = null;
                    logger.info("Going for dependencies");
                    for (ReportDependencies reportDependencies: reportModel.getDependencies()) {
                        logger.info("One dependency");
                        if (reportDependencies != null) {
                            logger.info("Report Dependency name {}", reportDependencies.getTitle());
                            if (jarFile.getName().contains(reportDependencies.getTitle())) {
                                reportDependency = reportDependencies;
                                logger.info("Found correspondence.");
                            }
                        }
                    }
                    logger.info("Finish dependencies");

                    ReportLicense reportLicense = new ReportLicense();
                    reportLicense.setName(license);
                    reportLicense.setOrigins(String.format("Commentary from %s file", fileName));

                    logger.info("Writing license to {}", reportDependency.getTitle());
                    reportDependency.setLicense(new ReportLicense[]{reportLicense});

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