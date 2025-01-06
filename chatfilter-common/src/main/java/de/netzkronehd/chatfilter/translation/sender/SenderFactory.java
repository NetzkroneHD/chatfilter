package de.netzkronehd.chatfilter.translation.sender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.UUID;

public abstract class SenderFactory<T> implements AutoCloseable {

    public abstract void sendMessage(T sender, Component message);
    public abstract void performCommand(T sender, String command);
    public abstract void showTitle(T sender, Title title);
    public abstract void resetTitle(T sender);

    public abstract UUID getUniqueId(T sender);
    public abstract String getName(T sender);
    public abstract boolean hasPermission(T sender, String permission);
    public abstract boolean isConsole(T sender);

    public final Sender wrap(T sender) {
        return new AbstractSender<>(this, sender);
    }

    @Override
    public void close() {

    }
}
