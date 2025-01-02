package de.netzkronehd.chatfilter.dependency;

public class DependencyNotDownloadedException extends Exception {
    public DependencyNotDownloadedException(String message) {
        super(message);
    }
}
