package de.netzkronehd.chatfilter.locale.translation.manager;

import de.netzkronehd.chatfilter.locale.translation.exception.UnknownLocaleException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TranslationManager {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private final Set<Locale> installed;
    private final Key key;
    private TranslationRegistry registry;

    public TranslationManager(String namespace, String value) {
        this.installed = ConcurrentHashMap.newKeySet();
        this.key = Key.key(namespace, value);
    }

    public TranslationManager(Key key) {
        this.installed = ConcurrentHashMap.newKeySet();
        this.key = key;
    }

    public void reload() {
        if (this.registry != null) {
            GlobalTranslator.translator().removeSource(this.registry);
            this.installed.clear();
        }
        this.registry = TranslationRegistry.create(this.key);
        this.registry.defaultLocale(DEFAULT_LOCALE);
        GlobalTranslator.translator().addSource(registry);
    }

    public Set<Locale> getInstalled() {
        return installed;
    }

    public Key getKey() {
        return key;
    }

    public TranslationRegistry getRegistry() {
        return registry;
    }

    public void loadFromFileSystem(Path directory) throws IOException, UnknownLocaleException {
        loadFromFileSystem(directory, (path) -> {});
    }

    public void loadFromFileSystem(Path directory, Consumer<Path> fileCallback) throws IOException, UnknownLocaleException {
        final List<Path> translationFiles;

        try (Stream<Path> stream = Files.list(directory)) {
            translationFiles = stream.filter(TranslationManager::isTranslationFile).toList();
        }

        final Map<Locale, ResourceBundle> loaded = new HashMap<>();
        for (Path translationFile : translationFiles) {
            fileCallback.accept(translationFile);
            final Map.Entry<Locale, ResourceBundle> result = loadTranslationFile(translationFile);
            loaded.put(result.getKey(), result.getValue());
        }
        loaded.forEach((locale, bundle) -> {
            final Locale localeWithoutCountry = new Locale(locale.getLanguage());
            if (!locale.equals(localeWithoutCountry) && !localeWithoutCountry.equals(DEFAULT_LOCALE) && this.installed.add(localeWithoutCountry)) {
                this.registry.registerAll(localeWithoutCountry, bundle, false);
            }
        });
    }

    public void registerTranslationFile(Path translationFile) throws IOException, UnknownLocaleException {
        final Map.Entry<Locale, ResourceBundle> result = loadTranslationFile(translationFile);
        final Locale locale = result.getKey();
        final ResourceBundle bundle = result.getValue();
        final Locale localeWithoutCountry = new Locale(locale.getLanguage());
        if (!locale.equals(localeWithoutCountry) && !localeWithoutCountry.equals(DEFAULT_LOCALE) && this.installed.add(localeWithoutCountry)) {
            this.registry.registerAll(localeWithoutCountry, bundle, false);
        }
    }

    public Map.Entry<Locale, ResourceBundle> loadTranslationFile(Path translationFile) throws IOException, UnknownLocaleException {
        final String fileName = translationFile.getFileName().toString();
        final String localeString = fileName.substring(0, fileName.length() - ".properties".length());
        final Locale locale = parseLocale(localeString);

        if (locale == null) {
            throw new UnknownLocaleException("Unknown locale '" + localeString + "' - unable to register.");
        }

        final PropertyResourceBundle bundle;
        try (BufferedReader reader = Files.newBufferedReader(translationFile, StandardCharsets.UTF_8)) {
            bundle = new PropertyResourceBundle(reader);
        }

        this.registry.registerAll(locale, bundle, false);
        this.installed.add(locale);
        return new AbstractMap.SimpleImmutableEntry<>(locale, bundle);
    }

    public static Component render(Component component, @Nullable Locale locale) {
        return GlobalTranslator.render(component, (locale == null ? DEFAULT_LOCALE:locale));
    }

    public static @Nullable Locale parseLocale(@Nullable String locale) {
        return locale == null ? null : Translator.parseLocale(locale);
    }

    public static boolean isTranslationFile(Path path) {
        return path.getFileName().toString().endsWith(".properties");
    }

}
