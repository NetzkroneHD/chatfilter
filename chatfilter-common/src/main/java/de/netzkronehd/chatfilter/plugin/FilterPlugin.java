package de.netzkronehd.chatfilter.plugin;

import de.netzkronehd.chatfilter.chain.FilterChain;
import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.Database;
import de.netzkronehd.chatfilter.dependency.Dependency;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.dependency.exception.DependencyDownloadException;
import de.netzkronehd.chatfilter.dependency.exception.DependencyNotDownloadedException;
import de.netzkronehd.chatfilter.exception.NoFilterChainException;
import de.netzkronehd.chatfilter.locale.MessagesProvider;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.config.ConfigLoader;
import de.netzkronehd.chatfilter.plugin.event.PlatformChatEvent;
import de.netzkronehd.chatfilter.locale.translation.exception.UnknownLocaleException;
import de.netzkronehd.chatfilter.locale.translation.sender.SenderFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public interface FilterPlugin {

    void registerCommands();

    default void loadConfig() throws IOException {
        getLogger().info("Loading config...");
        final ChatFilterConfig config = getFilterConfig();
        getConfigLoader().load(config);
        getFilterChain().loadFilters(config);
        getLogger().info("Loaded config. FilterChain has "+getFilterChain().getProcessors().size()+" processors.");
    }

    default void loadDatabase() throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        final Database database = getFilterConfig().getDatabaseConfig().createDatabase(getPluginDataFolder().resolve("chatfilter.db"));
        database.loadDriverClass(getDependencyManager());
        getLogger().info("Connecting to database using driver: "+database.getName()+"...");
        database.connect(getFilterConfig().getDatabaseConfig());
        getLogger().info("Creating tables...");
        database.createTables();
        setDatabase(database);
        getFilterConfig().setDatabaseConfig(null);
    }

    default void loadDependencies() throws DependencyDownloadException, IOException, InterruptedException, DependencyNotDownloadedException, ClassNotFoundException {
        getLogger().info("Loading dependencies...");
        for (Dependency dependency : Dependency.values()) {
            getLogger().info("Loading dependency: "+dependency.getMavenRepoPath());
            getDependencyManager().downloadDependency(dependency);
            getDependencyManager().loadDependency(dependency);
        }
        getLogger().info("Loaded dependencies.");
    }

    default void reload() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, UnknownLocaleException {
        loadConfig();
        loadDatabase();
        MessagesProvider.clear();
        MessagesProvider.loadFromFilePath(getPluginDataFolder().resolve("locales/"));
        MessagesProvider.setCurrentLocale(new Locale(getFilterConfig().getLocale()));
    }

    default void saveConfigsFromResources() {
        savePluginResource("blocked-patterns.yml", false);
        savePluginResource("filter.yml", false);
        savePluginResource("config.yml", false);
        savePluginResource("chatfilter.db", false);
        savePluginResource("locales/en.properties", false);
    }

    default void savePluginResource(String resourcePath, boolean replace) {
        final InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in the jar file");
        }

        final File pluginDataFolder = getPluginDataFolder().toFile();
        final File outFile = new File(pluginDataFolder, resourcePath);
        final int lastIndex = resourcePath.lastIndexOf('/');
        final File outDir = new File(pluginDataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                final OutputStream out = new FileOutputStream(outFile);
                final byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not save "+outFile.getName()+" to "+outFile, ex);
        }
    }

    void runSync(Runnable runnable);
    void runAsync(Runnable runnable);

    void callChatEvent(PlatformChatEvent event) throws NoFilterChainException;

    void setDatabase(Database database);

    Database getDatabase();
    FilterChain getFilterChain();
    ChatFilterConfig getFilterConfig();
    SenderFactory<?> getSenderFactory();
    ConfigLoader getConfigLoader();
    DependencyManager getDependencyManager();

    Optional<ChatFilterPlayer> getPlayer(UUID uuid);
    Optional<ChatFilterPlayer> getPlayer(String name);
    Collection<ChatFilterPlayer> getPlayers();
    Path getPluginDataFolder();
    Logger getLogger();
}
