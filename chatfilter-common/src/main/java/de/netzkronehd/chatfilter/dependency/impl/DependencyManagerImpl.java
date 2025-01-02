package de.netzkronehd.chatfilter.dependency.impl;

import de.netzkronehd.chatfilter.dependency.Dependency;
import de.netzkronehd.chatfilter.dependency.DependencyDownloadException;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.dependency.DependencyNotDownloadedException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DependencyManagerImpl implements DependencyManager {

    private static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2/";

    private final Path dependenciesFolder;
    private final Map<Dependency, URLClassLoader> loadedDependencies;
    private final HttpClient httpClient;

    public DependencyManagerImpl(Path dependenciesFolder) {
        this.dependenciesFolder = dependenciesFolder;
        this.loadedDependencies = new HashMap<>();
        this.httpClient = HttpClient.newHttpClient();
    }

    public URI getUri(Dependency dependency) {
        return URI.create(MAVEN_CENTRAL_URL + dependency.getMavenRepoPath());
    }

    @Override
    public void loadDependency(Dependency dependency) throws DependencyNotDownloadedException, MalformedURLException, ClassNotFoundException {
        if(isLoaded(dependency)) {
            return;
        }
        final File dependencyFile = getDependencyPath(dependency).toFile();
        if(!dependencyFile.exists()) {
            throw new DependencyNotDownloadedException("Dependency "+dependency.name()+" is not downloaded.");
        }
        final URL[] urls = new URL[]{dependencyFile.toURI().toURL()};
        final URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());

        if (dependency.getInitialClassDriver() == null) {
            return;
        }
        final Class<?> initialClassClass = loader.loadClass(dependency.getInitialClassDriver());
        Class.forName(dependency.getInitialClassDriver(), true, loader);


    }

    @Override
    public Path downloadDependency(Dependency dependency) throws IOException, InterruptedException, DependencyDownloadException {
        if(isDownloaded(dependency)) {
            return getDependencyPath(dependency);
        }
        final byte[] bytes = downloadRaw(dependency);
        checkChecksum(dependency, bytes);
        final Path dependencyPath = dependenciesFolder.resolve(dependency.getFileName());
        Files.write(dependencyPath, bytes);
        return dependencyPath;
    }

    @Override
    public Path getDependencyPath(Dependency dependency) {
        return dependenciesFolder.resolve(dependency.getFileName());
    }

    @Override
    public boolean isLoaded(Dependency dependency) {
        return loadedDependencies.containsKey(dependency);
    }

    @Override
    public boolean isDownloaded(Dependency dependency) {
        return Files.exists(getDependencyPath(dependency));
    }

    private byte[] downloadRaw(Dependency dependency) throws DependencyDownloadException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(dependency))
                .GET()
                .build();

        final HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new DependencyDownloadException("Failed to download dependency " + dependency.name()+". Status code: "+response.statusCode());
        }
        return response.body();
    }

    private void checkChecksum(Dependency dependency, byte[] bytes) throws DependencyDownloadException {
        final byte[] hash = createDigest().digest(bytes);
        if (!dependency.checksumMatches(hash)) {
            throw new DependencyDownloadException(
                    "Checksum does not match for dependency " + dependency.name()+
                    ". Expected: '"+ Base64.getEncoder().encodeToString(dependency.getChecksum()) +
                    "', got: '"+Base64.getEncoder().encodeToString(hash)+"'");
        }
    }

    public MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
