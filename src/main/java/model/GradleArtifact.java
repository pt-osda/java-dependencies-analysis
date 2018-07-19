package model;

import org.gradle.api.artifacts.ResolvedDependency;
import java.util.HashSet;
import java.util.Set;

public class GradleArtifact {
    /**
     * Indicates the parent of the artifact.
     */
    private final GradleArtifact parent;

    /**
     * Indicates all the children of the artifact.
     */
    private final Set<GradleArtifact> children;

    /**
     * Indicates the group Id of the artifact.
     */
    private final String group;

    /**
     * Indicates the name, artifactId, of the artifact.
     */
    private final String name;

    /**
     * Indicates the version of the artifact.
     */
    private final String version;

    public GradleArtifact getParent() {
        return parent;
    }

    public Set<GradleArtifact> getChildren() {
        return children;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public GradleArtifact(GradleArtifact parent, ResolvedDependency resolvedDependency){
        this.parent = parent;
        children = new HashSet<>();
        group = resolvedDependency.getModule().getId().getGroup();
        name = resolvedDependency.getModule().getId().getName();
        version = resolvedDependency.getModule().getId().getVersion();

        for (ResolvedDependency childrenDependency : resolvedDependency.getChildren()) {
            GradleArtifact child = new GradleArtifact(this, childrenDependency);
            children.add(child);
        }
    }
}