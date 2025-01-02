package de.netzkronehd.chatfilter.dependency;

import lombok.Getter;

import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;

@Getter
public enum Dependency {

    // Libraries
    COMMONS_CLI(
            "commons-cli",
            "commons-cli",
            "1.9.0",
            ""
    ),

    // Remote databases
    MYSQL(
            "com{}mysql",
            "mysql-connector-j",
            "9.1.0",
            ""
    ),
    POSTGRESQL(
            "org{}postgresql",
            "postgresql",
            "42.7.4",
            ""
    ),

    // Local databases
    SQLITE(
            "org.xerial",
            "sqlite-jdbc",
            "3.47.1.0",
            ""
    );

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    private final String mavenRepoPath;
    private final String version;
    private final byte[] checksum;

    Dependency(String groupId, String artifactId, String version, String checksum) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );
        this.version = version;
        this.checksum = Base64.getDecoder().decode(checksum);
    }

    public String getFileName(String classifier) {
        final String name = name().toLowerCase(Locale.ROOT).replace('_', '-');
        final String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;

        return name + "-" + this.version + extra + ".jar";
    }

    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

}
