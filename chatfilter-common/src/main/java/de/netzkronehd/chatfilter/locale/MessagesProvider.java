package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.chatfilter.locale.translation.exception.UnknownLocaleException;
import de.netzkronehd.chatfilter.locale.translation.manager.TranslationManager;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


public class MessagesProvider {

    private static final Map<Locale, Map<String, String>> LOCALES = new HashMap<>();
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static Locale CURRENT_LOCALE = DEFAULT_LOCALE;

    public static void addMessage(Locale locale, String key, String value) {
        LOCALES.computeIfAbsent(locale, k -> new HashMap<>()).put(key, value);
    }

    public static void addMessages(Locale locale, Map<String, String> messages) {
        LOCALES.computeIfAbsent(locale, k -> new HashMap<>()).putAll(messages);
    }

    public static String translate(Locale locale, String key) {
        final Map<String, String> messages = LOCALES.get(locale);
        if (messages == null) {
            return key;
        }
        final String message = messages.get(key);
        if (message == null) {
            return key;
        }
        return message;
    }

    public static String translate(String key) {
        return translate(CURRENT_LOCALE, key);
    }

    public static void addMessages(Locale locale, Properties properties) {
        properties.forEach((key, value) -> addMessage(locale, (String) key, (String) value));
    }

    public static void loadFromFile(Locale locale, Path file) throws IOException {
        final Properties properties = new Properties();
        try (final BufferedReader reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        }
        addMessages(locale, properties);
    }

    public static void loadFromFilePath(Path path) throws IOException, UnknownLocaleException {
        final List<Path> list;
        try(final Stream<@NotNull Path> fileStream = Files.list(path)) {
            list = fileStream.toList();
        }
        for (Path file : list) {
            loadFromFile(file);
        }
    }

    public static void loadFromFile(Path file) throws UnknownLocaleException, IOException {
        final String fileName = file.getFileName().toString();
        final String localeString = fileName.substring(0, fileName.length() - ".properties".length());
        final Locale locale = TranslationManager.parseLocale(localeString);
        if (locale == null) {
            throw new UnknownLocaleException("Unknown locale '" + localeString + "' - unable to register.");
        }
        loadFromFile(locale, file);
    }

    public static void clear() {
        LOCALES.forEach((locale, messages) -> messages.clear());
        LOCALES.clear();
    }

    public static Map<Locale, Map<String, String>> getLocales() {
        return LOCALES;
    }

    public static void setCurrentLocale(Locale currentLocale) {
        Objects.requireNonNull(currentLocale);
        MessagesProvider.CURRENT_LOCALE = currentLocale;
    }
}
