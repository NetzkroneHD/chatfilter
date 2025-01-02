package de.netzkronehd.chatfilter.dependency.impl;

import de.netzkronehd.chatfilter.dependency.Dependency;
import de.netzkronehd.chatfilter.dependency.DependencyManager;

import java.io.IOException;
import java.net.URI;
import java.net.URLClassLoader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DependencyManagerImpl implements DependencyManager {

    private static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2/";

    private final Map<Dependency, URLClassLoader> loadedDependencies;
    private final HttpClient httpClient;

    public DependencyManagerImpl() {
        this.loadedDependencies = new HashMap<>();
        this.httpClient = HttpClient.newHttpClient();
    }

    public URI getUri(Dependency dependency) {
        return URI.create(MAVEN_CENTRAL_URL + dependency.getMavenRepoPath());
    }

    public byte[] downloadRaw(Dependency dependency) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(dependency))
                .GET()
                .build();

        final HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // TODO

        return null;
    }

    @Override
    public void loadDependency(Dependency dependency) {

    }

    @Override
    public Path downloadDependency(Dependency dependency) {
        if(isDownloaded(dependency)) {
            return getDependencyPath(dependency);
        }
        return null;
    }

    @Override
    public Path getDependencyPath(Dependency dependency) {
        return null;
    }

    @Override
    public boolean isLoaded(Dependency dependency) {
        return false;
    }

    @Override
    public boolean isDownloaded(Dependency dependency) {
        return false;
    }
}
