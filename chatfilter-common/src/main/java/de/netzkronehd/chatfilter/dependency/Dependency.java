package de.netzkronehd.chatfilter.dependency;

import lombok.Getter;

import java.util.Arrays;
import java.util.Base64;

@Getter
public enum Dependency {

    // Remote databases
    MYSQL(
            "com{}mysql",
            "mysql-connector-j",
            "9.1.0",
            "h3bi68RgcsmkfqWdmCmMQnO9nxansmtd+kdEU1qibGI=",
            "com.mysql.cj.jdbc.Driver"),
    POSTGRESQL(
            "org{}postgresql",
            "postgresql",
            "42.7.4",
            "GIl2ch6tjoYn622DidUA3MwMm+vYhSaKMEcYAnSmAx4=",
            "org.postgresql.Driver"),

    // Local databases
    SQLITE(
            "org.xerial",
            "sqlite-jdbc",
            "3.47.1.0",
            "QWTZU0euq0K3VMq7eUW5PlTyndsu3tln2+l5tARctoU=",
            "org.sqlite.JDBC");

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    private final String mavenRepoPath;
    private final String version;
    private final byte[] checksum;
    private final String initialClassDriver;

    Dependency(String groupId, String artifactId, String version, String checksum, String initialClassDriver) {
        this.initialClassDriver = initialClassDriver;
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

    public String getFileName() {
        final String name = name().toLowerCase().replace('_', '-');
        return name+"-"+this.version+".jar";
    }

    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

}
