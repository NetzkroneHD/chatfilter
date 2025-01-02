package de.netzkronehd.chatfilter.dependency;

import de.netzkronehd.chatfilter.dependency.exception.DependencyDownloadException;
import de.netzkronehd.chatfilter.dependency.exception.DependencyNotDownloadedException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Optional;

public interface DependencyManager {

    void downloadAllDependencies() throws DependencyDownloadException, IOException, InterruptedException;
    void loadAllDependencies() throws IOException, DependencyNotDownloadedException, ClassNotFoundException;

    Class<?> loadDependency(Dependency dependency) throws DependencyNotDownloadedException, MalformedURLException, ClassNotFoundException;

    Optional<Class<?>> getClassLoader(Dependency dependency);
    Path downloadDependency(Dependency dependency) throws IOException, InterruptedException, DependencyDownloadException;

    Path getDependencyPath(Dependency dependency);
    boolean isLoaded(Dependency dependency);

    boolean isDownloaded(Dependency dependency);
}
