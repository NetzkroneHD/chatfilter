package de.netzkronehd.chatfilter.dependency;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface DependencyManager {

    void loadDependency(Dependency dependency) throws DependencyNotDownloadedException, MalformedURLException, ClassNotFoundException;

    Path downloadDependency(Dependency dependency) throws IOException, InterruptedException, DependencyDownloadException;
    Path getDependencyPath(Dependency dependency);

    boolean isLoaded(Dependency dependency);
    boolean isDownloaded(Dependency dependency);
}
