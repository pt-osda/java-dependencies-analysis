import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import model.Policy;
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
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class DependenciesLicenses {
    private static final HashMap<String, String> KNOWN_LICENSE = new HashMap<>();
    private static final List<String> PROCESSED_FILES = new LinkedList<>();

    /**
     * Main method for the search of the licenses of the project dependencies. After preparing the map of licenses to
     * their representation, the configurations of the project that can be resolved will be searched for the jar files
     * of the dependencies.
     * @param configurationContainer    reference to obtain the configurations of the project. That is their artifacts
     *                                  and  dependencies
     * @param reportModel   The report model that represents every information found.
     * @param policy    The object that represents the policy of the project, indicating values necessary to the correct
     *                  function of the plugin.
     * @param executor  The reference to the threadPool where the analysis of a JarFile will happen
     * @param finalExecutor The reference to the thread that is responsible for write any changes found in the
     *                      reportModel.
     * @param logger    A reference to the plugin logger.
     */
    public static void findDependenciesLicenses(ConfigurationContainer configurationContainer, ReportModel reportModel, Policy policy, ExecutorService executor, ExecutorService finalExecutor, Logger logger){
        fillLicenses();

        configurationContainer.stream().filter(Configuration::isCanBeResolved)
                .forEach(configuration -> {
                    logger.info("Running for configuration {}.", configuration.getName());
                    Set<File> files = configuration.resolve();

                    logger.info("Beginning to get all {} configuration files.", files.size());

                    for (File currentFile : files) {
                        String absoluteFilePath = currentFile.getAbsolutePath();
                        logger.info("\n\nThe current file is {}.", absoluteFilePath);

                        try (JarFile jarFile = new JarFile(absoluteFilePath)) {
                            logger.info("Reading jar file.");

                            logger.info("JarFile name {}.", jarFile.getName());

                            if (!PROCESSED_FILES.contains(jarFile.getName())) {
                                logger.info("Processing.");

                                PROCESSED_FILES.add(jarFile.getName());
                                executor.submit(() -> {
                                    try {
                                        findRequiredFiles(absoluteFilePath, reportModel, policy, finalExecutor, logger);
                                    } catch (IOException e) {
                                        logger.warn("An exception occurred when attempting to get the Jar file from path {}. The exception was {}", absoluteFilePath, e.getMessage());
                                    }
                                });
                            }
                        } catch (Exception e) {
                            logger.warn("Occurred an exception when attempting to process a jar file in search of a license file, {}.", e.getMessage());
                        }
                    }

                    logger.info("All configuration files were shown.");
                }
        );
        PROCESSED_FILES.clear();
    }

    /**
     * Search in the jar file for the files License and .pom. So that they can be analyzed in search of the dependency
     * license.
     * @param reportModel   The report model that represents every information found.
     * @param policy    The object that represents the policy of the project, indicating values necessary to the correct
     *                  function of the plugin.
     * @param finalExecutor    The reference to the thread that is responsible for write any changes found in the
     *                         reportModel.
     * @param logger    A reference to the plugin logger.
     */
    private static void findRequiredFiles(String absoluteFilePath, ReportModel reportModel, Policy policy, ExecutorService finalExecutor, Logger logger) throws IOException {
        JarFile jar = new JarFile(absoluteFilePath);
        final Enumeration<JarEntry> entries = jar.entries();
        int files = 0;

        while (entries.hasMoreElements() && files < 2) {
            final JarEntry entry = entries.nextElement();
            final String entryName = (new File(entry.getName())).getName().toLowerCase(); // the last name of the file or directory
            if (!entry.isDirectory()){
                if (entryName.equals("pom.xml")) {
                    files++;
                    logger.info("POM file found: {}.", entry.getName());
                    try {
                        validatePomFile(jar, entry.getName(), reportModel, policy, finalExecutor, logger);
                    } catch(IOException e) {
                        logger.warn("An IOException happened while validating the pom object {}.", e.getMessage());
                    }
                    try {
                        validateLicenseFromFile(jar, entry.getName(), reportModel, policy, finalExecutor, logger);
                    } catch (IOException e) {
                        logger.warn("An IOException happened while validating the pom file {}.", e.getMessage());
                    }
                }
                else if (entryName.contains("license")){
                    files++;
                    logger.info("LICENSE File found: {}.", entry.getName());
                    try {
                        validateLicenseFromFile(jar, entry.getName(), reportModel, policy, finalExecutor, logger);
                    } catch (IOException e) {
                        logger.warn("An IOException happened while validating the License file {}.", e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Converts the .pom file in a pom model so that the list of licenses indicated can be search for the licenses known.
     * <br>
     * The list of license in the .pom file is indicated as follow:
     *
     *<pre>
     *{@code
     * <licenses>
     *              <license>
     *                  <name>Apache Software Licenses</name>
     *                  <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
     *              </license>
     *          </licenses>
     *}
     *</pre>
     * @param jar   The jar file that contains the .pom file.
     * @param pomFile   The path to the .pom file in the jar file.
     * @param reportModel   The report model where the license found will be added.
     * @param policy    The object that represents the policy of the project, indicating values necessary to the correct
     *                  function of the plugin.
     * @param finalExecutor    The thread where the addition of the license will be done.
     * @param logger    A reference to the plugin logger.
     * @throws IOException thrown by xmlMapper.readValue
     */
    private static void validatePomFile(JarFile jar, String pomFile, ReportModel reportModel, Policy policy, ExecutorService finalExecutor, Logger logger) throws IOException {
        ZipEntry entryPomFile = jar.getEntry(pomFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entryPomFile)));
        XmlMapper xmlMapper = new XmlMapper();

        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        POMModel pomModel = xmlMapper.readValue(reader, POMModel.class);

        logger.info("Pom Model licenses: {}", pomModel.getLicenses().toString());

        for (CustomLicense license : pomModel.getLicenses()) {
            String licenseFound = "";
            if (KNOWN_LICENSE.containsKey(license.getName()))
                licenseFound = KNOWN_LICENSE.get(license.getName());

            else if (KNOWN_LICENSE.containsKey(license.getUrl()))
                licenseFound = KNOWN_LICENSE.get(license.getUrl());

            if (!licenseFound.equals("")) {
                String addingLicense = licenseFound;
                finalExecutor.submit(() -> {
                    List<ReportDependencies> reportDependency = reportModel.getDependencies()
                            .stream()
                            .filter(reportDependencies -> jar.getName().contains(reportDependencies.getTitle()))
                            .collect(Collectors.toList());

                    ReportLicense reportLicense = new ReportLicense();
                    reportLicense.setSpdxId(addingLicense);
                    reportLicense.setSource("Tag License from pom file.");
                    reportLicense.setValid(!policy.getInvalidLicenses().contains(addingLicense));

                    reportDependency.get(0).addLicense(reportLicense);
                });
            }
        }
    }

    /**
     * Analyzes the referenced file in search of a keyword that indicates the license in use.
     * @param jarFile   The Jar file where the file is
     * @param fileName  The name of the file to be analyzed.
     * @param reportModel   The report model where the license found will be added.
     * @param policy    The object that represents the policy of the project, indicating values necessary to the correct
     *                  function of the plugin.
     * @param finalExecutor    The thread where the addition of the license will be done.
     * @param logger    A reference to the plugin logger.
     * @throws IOException thrown by read.readLine
     */
    private static void validateLicenseFromFile(JarFile jarFile, String fileName, ReportModel reportModel, Policy policy, ExecutorService finalExecutor, Logger logger) throws IOException {
        ZipEntry entryFile = jarFile.getEntry(fileName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entryFile)));

        String line;
        while ((line = reader.readLine()) != null) {
            for (String keysLicense : KNOWN_LICENSE.keySet()) {
                if (line.contains(keysLicense)) {
                  logger.info("Line contains key: {}", keysLicense);
                  String license = KNOWN_LICENSE.get(keysLicense);

                  finalExecutor.submit(() -> {
                      logger.info("Jar file name {}", jarFile.getName());

                      List<ReportDependencies> reportDependency = reportModel.getDependencies()
                              .stream()
                              .filter(dependency -> {
                                  String[] parts = dependency.getTitle().split(":");
                                  return jarFile.getName().contains(Paths.get(parts[0], parts[1]).toString());
                              })
                              .collect(Collectors.toList());

                      logger.info("Finish dependencies");
                      logger.info("License {}", license);
                      logger.info("File {}", fileName);

                      ReportLicense reportLicense = new ReportLicense();
                      reportLicense.setSpdxId(license);
                      reportLicense.setSource(String.format("Indicated in %s file as %s", fileName, keysLicense));
                      reportLicense.setValid(!policy.getInvalidLicenses().contains(license));

                      logger.info("Current licenses {}", reportDependency.get(0).getLicenses().toString());
                      logger.info("Writing license number {} to {}", reportDependency.get(0).getLicenses().size() + 1, reportDependency.get(0).getTitle());
                      reportDependency.get(0).addLicense(reportLicense);
                  });

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
        KNOWN_LICENSE.put("Apache Software License, Version 1.1", "Apache-1.1");
        KNOWN_LICENSE.put("Apache License, Version 2.0", "Apache-2.0");
        KNOWN_LICENSE.put("http://www.apache.org/licenses/LICENSE-2.0", "Apache-2.0");
        KNOWN_LICENSE.put("https://opensource.org/licenses/BSD-2-Clause", "BSD-2-Clause");
        KNOWN_LICENSE.put("https://opensource.org/licenses/BSD-3-Clause", "BSD-3-Clause");
        KNOWN_LICENSE.put("BSD License", "BSD-3-Clause");  // hamcrest-core1.3 from consul-api
        KNOWN_LICENSE.put("BSD 3-clause license", "BSD-3-Clause");  // antlr4-runtime from kafka-connect-query-language
        KNOWN_LICENSE.put("Creative Commons Legal Code", "CC0-1.0");
        KNOWN_LICENSE.put("http://repository.jboss.org/licenses/cc0-1.0.txt", "CC0-1.0");
        KNOWN_LICENSE.put("Common Public License - v 1.0", "CPL-1.0");
        KNOWN_LICENSE.put("https://www.eclipse.org/legal/cpl-v10.html", "CPL-1.0");
        KNOWN_LICENSE.put("Eclipse Public License - v 1.0", "EPL-1.0");
        KNOWN_LICENSE.put("https://www.eclipse.org/legal/epl-v10.html", "EPL-1.0");
        KNOWN_LICENSE.put("Eclipse Public License - v 2.0", "EPL-2.0");
        KNOWN_LICENSE.put("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt", "EPL-2.0");
        KNOWN_LICENSE.put("GNU GENERAL PUBLIC LICENSE, Version 1", "GPL-1.0");
        KNOWN_LICENSE.put("https://www.gnu.org/licenses/gpl-1.0", "GPL-1.0");
        KNOWN_LICENSE.put("GNU GENERAL PUBLIC LICENSE, Version 2", "GPL-2.0");
        KNOWN_LICENSE.put("https://www.gnu.org/licenses/gpl-2.0", "GPL-2.0");
        KNOWN_LICENSE.put("GNU GENERAL PUBLIC LICENSE, Version 3", "GPL-3.0");
        KNOWN_LICENSE.put("https://www.gnu.org/licenses/gpl-3.0", "GPL-3.0");
        KNOWN_LICENSE.put("GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1", "LGPL-2.1");
        KNOWN_LICENSE.put("https://www.gnu.org/licenses/lgpl-2.1", "LGPL-2.1");
        KNOWN_LICENSE.put("GNU LESSER GENERAL PUBLIC LICENSE, Version 3", "LGPL-3.0");
        KNOWN_LICENSE.put("https://opensource.org/licenses/MIT", "MIT");
        KNOWN_LICENSE.put("MIT License", "MIT");
        KNOWN_LICENSE.put("https://www.mozilla.org/en-US/MPL/1.1", "MPL-1.1");
        KNOWN_LICENSE.put("Mozilla Public License Version 1.1", "MPL-1.1");
        KNOWN_LICENSE.put("https://www.mozilla.org/en-US/MPL/2.0", "MPL-2.0");
        KNOWN_LICENSE.put("Mozilla Public License, Version 2.0", "MPL-2.0");
    }
}
