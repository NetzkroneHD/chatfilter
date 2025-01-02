package de.netzkronehd.chatfilter.dependency;

import java.nio.file.Path;

public interface DependencyManager {

    void loadDependency(Dependency dependency);

    Path downloadDependency(Dependency dependency);
    Path getDependencyPath(Dependency dependency);

    boolean isLoaded(Dependency dependency);
    boolean isDownloaded(Dependency dependency);
}
