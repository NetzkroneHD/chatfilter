package de.netzkronehd.chatfilter.dependency;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

     public static void main(String[] args) {
        try {
            // Verzeichnis und JAR-Datei hinzufügen
            File libFolder = new File("lib");
            File sqliteJar = new File(libFolder, "sqlite-3.2.1.jar");

            // Erstelle einen URLClassLoader, der das JAR zur Laufzeit lädt
            URL[] urls = new URL[] { sqliteJar.toURI().toURL() };
            URLClassLoader loader = new URLClassLoader(urls, Main.class.getClassLoader());

            // Lade die SQLite JDBC-Klasse zur Laufzeit
            Class<?> sqliteClass = loader.loadClass("org.sqlite.JDBC");

            // Initialisiere den Treiber
            Class.forName("org.sqlite.JDBC", true, loader);
            System.out.println("SQLite JDBC Treiber geladen.");

            // Jetzt kannst du mit der SQLite-Datenbank arbeiten, als ob der Treiber im Classpath wäre
            // Dein SQLite-Code hier...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
