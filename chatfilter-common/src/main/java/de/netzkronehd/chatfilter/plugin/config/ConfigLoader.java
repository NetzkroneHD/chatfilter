package de.netzkronehd.chatfilter.plugin.config;

import de.netzkronehd.chatfilter.config.ChatFilterConfig;

import java.io.IOException;

public interface ConfigLoader {

    void load(ChatFilterConfig config) throws IOException;

}
