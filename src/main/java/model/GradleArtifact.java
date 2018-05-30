package model;

import org.gradle.api.artifacts.ResolvedDependency;
import java.util.HashSet;
import java.util.Set;

public class GradleArtifact {
    private final GradleArtifact parent;
    private final Set<GradleArtifact> children;
    private final String group;
    private final String name;
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