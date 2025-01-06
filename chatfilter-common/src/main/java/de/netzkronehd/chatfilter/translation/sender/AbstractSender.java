package de.netzkronehd.chatfilter.translation.sender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.UUID;

public class AbstractSender<T> implements Sender {

    private final SenderFactory<T> factory;
    private final T sender;

    private final UUID uniqueId;
    private final String name;
    private final boolean isConsole;

    public AbstractSender(SenderFactory<T> factory, T sender) {
        this.factory = factory;
        this.sender = sender;
        this.uniqueId = this.factory.getUniqueId(this.sender);
        this.name = this.factory.getName(this.sender);
        this.isConsole = this.factory.isConsole(this.sender);
    }

    @Override
    public void sendMessage(Component message) {
        this.factory.sendMessage(this.sender, message);
    }

    @Override
    public void performCommand(String command) {
        this.factory.performCommand(this.sender, command);
    }

    @Override
    public void showTitle(Title title) {
        this.factory.showTitle(this.sender, title);
    }

    @Override
    public void resetTitle() {
        this.factory.resetTitle(this.sender);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.factory.hasPermission(this.sender, permission);
    }

    public SenderFactory<T> getFactory() {
        return factory;
    }

    public T getSender() {
        return sender;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isConsole() {
        return isConsole;
    }

}
