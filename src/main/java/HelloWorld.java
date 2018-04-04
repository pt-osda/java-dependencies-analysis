import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.logging.Logger;
import org.gradle.internal.impldep.org.apache.maven.model.Model;
import org.gradle.internal.impldep.org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.gradle.internal.impldep.org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloWorld implements Plugin<Project> {
    private final String MavenCentralRepositoryUrl = "https://search.maven.org/remotecontent?filepath=";
    @Override
    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.info(String.format("Hello World my getName is %s", project.getName()));

        //project.getConfigurations().getByName("poms").getArtifacts().getFiles().
        try {
            PrintWriter writer = new PrintWriter("Report.txt");
            BufferedReader reader = new BufferedReader(new FileReader("Prototipo.txt"));

            String input = reader.readLine();
            StringBuilder result = new StringBuilder();

            while(input != null){
                result.append(input);
                result.append(System.lineSeparator());
                input = reader.readLine();
            }
            logger.info("Finish reading file");
            writer.print(result.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        ArtifactResolutionResult artifactResolutionResult = project.getDependencies()
                                                            .createArtifactResolutionQuery()
                                                            .forComponents()
                                                            .withArtifacts(MavenModule.class, MavenPomArtifact.class).execute();

        logger.info("Start Components analyse");

        for (ComponentArtifactsResult component: artifactResolutionResult.getResolvedComponents()) {
            component.getArtifacts(MavenPomArtifact.class).forEach((element) -> logger.info(String.format("POM file for %s: %s", component.getId(), element.getId())));
        }

        logger.info("Finish Components analyse");
*/
        ConfigurationContainer configurationContainer = project.getConfigurations();
        Configuration configuration = configurationContainer.getByName("compile");

        DependencySet dependencySet = configuration.getDependencies();

        for (Dependency dependency : dependencySet) {
            logger.info(String.format("Compile Configuration Dependency name=%s, group=%s, version=%s", dependency.getName(), dependency.getGroup(), dependency.getVersion()));
            String dependencyURL = produceURL(dependency.getName(), dependency.getGroup(), dependency.getVersion());
            logger.info(dependencyURL);

            try {
                URL url = new URL(dependencyURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");


                Model model = null;
                FileReader reader = null;
                MavenXpp3Reader mavenreader = new MavenXpp3Reader();
                try {
                    reader = new FileReader(String.valueOf(con.getInputStream()));
                    model = mavenreader.read(reader);
                    //model.setPomFile(pomfile);
                }catch(Exception ex){}

                ObjectInputStream objectInputStream = new ObjectInputStream(con.getInputStream());
                model.Project receivedProject = (model.Project) objectInputStream.readObject();
                /*BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();*/
                logger.info("Http Response: " + receivedProject);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            //Project
            //dependency.get
        }
    }

    private String produceURL(String name, String group, String version){
        String urlgroup = group.replaceAll("\\.", "/");
        return MavenCentralRepositoryUrl + urlgroup + "/" + name + "/" + version + "/" + name + "-" + version + ".pom";
    }
}