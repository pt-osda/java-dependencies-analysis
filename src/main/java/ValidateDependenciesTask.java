import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

// TODO no model para o ficheiro incluir o groupId e o artifactId e depois nas vulnerabilidades ir buscar informação a esse model
public class ValidateDependenciesTask extends AbstractTask {
    private final HashMap<String, String> KNOWN_LICENSE = new HashMap<>();
    private final List<String> VALID_LICENSE_LIST = new LinkedList<>();
    private final HashMap<Artifacts, Artifacts> dependenciesPackages = new HashMap<>();
    private final List<Artifacts> REQUEST_BODY = new LinkedList<>();
    private final List<ReportDependencies> REPORT_DEPENDENCIES = new ArrayList<>();
    private final String API_URL = "http://localhost:8080/gradle/dependency/vulnerabilities";
    private final String API_REPORT_URL = "http://localhost:8080/report";
    private Logger logger;
    private String pomFile;
    private String licenseFile;
    private boolean invalidLicense = false;
    private ReportModel reportModel;
    private CloseableHttpClient httpClient;

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

            /*try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File("Report.json"), reportModel);
            } catch (IOException e) {
                logger.info( "An exception occurred. {}", e.getMessage());
                e.printStackTrace();
            }*/

            reportModel.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
            CloseableHttpResponse response = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                String report = mapper.writeValueAsString(reportModel);

                HttpPost httpPost = new HttpPost(API_REPORT_URL);
                httpPost.setEntity(new StringEntity(report));
                httpPost.addHeader("Content-Type", "application/json");

                logger.info("Object to write {}", report);

                response = httpClient.execute(httpPost);

                logger.info("Response Status {}", response.getStatusLine().getStatusCode());

                logger.info("Response {}", response.getStatusLine());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates in the report object all the dependencies in the project.
     * @param configurationContainer
     */
    private void getAllDependencies(ConfigurationContainer configurationContainer) {
        List<ReportDependencies> foundDependencies = new LinkedList<>();

        configurationContainer
                //.stream()
                //.filter(configuration -> configuration.getName().equals("compile"))
                .forEach(configuration -> {
                    logger.info("Configuration {}", configuration.getName());
                    configuration.getDependencies().forEach(dependency -> {
                        List<ReportDependencies> repeatedDependencies = foundDependencies
                                .stream()
                                .filter(reportDependencies -> reportDependencies.getTitle().equals(dependency.getName()))
                                .distinct()
                                .collect(Collectors.toList());

                        logger.info("ReportDependency found {}", repeatedDependencies.size());

                        if (repeatedDependencies.isEmpty()) {

                            ReportDependencies newDependency = new ReportDependencies(dependency.getGroup() + ":" + dependency.getName(), dependency.getVersion());
                            foundDependencies.add(newDependency);
                            logger.info("Added dependency {}", newDependency);
                        }
                    });
                });
        //reportModel.setDependencies(foundDependencies.toArray(new ReportDependencies[0]));
        reportModel.setDependencies(foundDependencies);
    }

    private void getVulnerabilities(ConfigurationContainer configurationContainer) {
        Set<GradleArtifact> gradleArtifacts = configurationContainer
                .stream()
                .filter(configuration -> configuration.getName().equals("compile")) // TODO remove from final form
                .flatMap(configuration -> configuration.getResolvedConfiguration().getFirstLevelModuleDependencies().stream())
                .distinct()
                .map(resolvedDependency -> new GradleArtifact(null, resolvedDependency))
                .collect(Collectors.toSet());

        requestChild(null, gradleArtifacts);

        CloseableHttpResponse response = null;
        try {
            logger.info("Request body {}", REQUEST_BODY.toString());

            ObjectMapper mapper = new ObjectMapper();
            String obj = mapper.writeValueAsString(REQUEST_BODY);
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setEntity(new StringEntity(obj));
            httpPost.addHeader("Content-Type", "application/json");

            logger.info("Object to write {}", obj);

            response = httpClient.execute(httpPost);

            logger.info("Response Status {}", response.getStatusLine().getStatusCode());

            logger.info("Response {}", response.getStatusLine());

            VulnerabilitiesResult[] vulnerabilities = mapper.readValue(response.getEntity().getContent(), VulnerabilitiesResult[].class);

            for (VulnerabilitiesResult vulnerability : vulnerabilities) {
                logger.info("Entity {}", vulnerability);
                ReportDependencies dependencies = new ReportDependencies(vulnerability.getTitle(), vulnerability.getMainVersion());

                List<ReportDependencies> reportDependencies = reportModel.getDependencies().stream().filter(dependency -> dependency.equals(dependencies)).collect(Collectors.toList());

                if (!reportDependencies.isEmpty()){
                    reportDependencies.get(0).setVulnerabilities(vulnerability.getVulnerabilities().toArray(new ReportVulnerabilities[0]));
                    reportDependencies.get(0).setVulnerabilities_count(vulnerability.getTotalVulnerabilities());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestChild(Artifacts currentArtifact, Set<GradleArtifact> children) {
        children.forEach(gradleArtifact -> {

            Artifacts artifact = new Artifacts(gradleArtifact.getName(), gradleArtifact.getVersion(), gradleArtifact.getGroup());
            logger.info("Artifact {}", currentArtifact);

            if (!dependenciesPackages.containsKey(artifact)){
                logger.info("Gradle Artifact Child group {}, name {}, version {}", gradleArtifact.getGroup(), gradleArtifact.getName(), gradleArtifact.getVersion());
                REQUEST_BODY.add(artifact);
                dependenciesPackages.put(artifact, currentArtifact);

                // TODO modify to show dependency tree
                ReportDependencies reportDependency = new ReportDependencies(gradleArtifact.getGroup() + ":" + gradleArtifact.getName(), gradleArtifact.getVersion());

                if (!reportModel.getDependencies().contains(reportDependency))
                    reportModel.getDependencies().add(reportDependency);

                requestChild(artifact, gradleArtifact.getChildren());
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
                    List<ReportDependencies> reportDependency = reportModel.getDependencies().stream().filter(reportDependencies -> jar.getName().contains(reportDependencies.getTitle())).collect(Collectors.toList());
                    logger.info("Report Dependency found {}", reportDependency.size());

                    ReportLicense reportLicense = new ReportLicense();
                    reportLicense.setSpdx_id(licenseFound);
                    reportLicense.setSource("Tag License from pom file.");

                    reportDependency.get(0).setLicenses(new ReportLicense[]{reportLicense});

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
                    logger.info("There are {} dependencies ready to write", reportModel.getDependencies().size());
                    logger.info("Jar file name {}", jarFile.getName());

                    ReportDependencies reportDependency = null;
                    logger.info("Going for dependencies");
                    for (ReportDependencies reportDependencies: reportModel.getDependencies()) {
                        logger.info("One dependency");
                        if (reportDependencies != null) {
                            logger.info("Report Dependency name {}", reportDependencies.getTitle());
                            if (jarFile.getName().contains(reportDependencies.getTitle().replace(":", "\\"))) {
                                reportDependency = reportDependencies;
                                logger.info("Found correspondence.");
                                break;
                            }
                        }
                    }

                    logger.info("Finish dependencies");
                    logger.info("License {}", license);
                    logger.info("File {}", fileName);

                    ReportLicense reportLicense = new ReportLicense();
                    reportLicense.setSpdx_id(license);
                    reportLicense.setSource(String.format("Commentary from %s file", fileName));

                    logger.info("Produced report license");

                    logger.info("Writing license to {}", reportDependency.getTitle());
                    reportDependency.setLicenses(new ReportLicense[]{reportLicense});

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