package de.netzkronehd.chatfilter.translation.sender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.UUID;

public interface Sender {

    /** The uuid used by the console sender. */
    UUID CONSOLE_UUID = new UUID(0, 0); // 00000000-0000-0000-0000-000000000000
    /** The name used by the console sender. */
    String CONSOLE_NAME = "Console";

    UUID NETZKRONEHD_UUID = UUID.fromString("15d53d1e-79cb-4c24-bd14-07c792cfd08a");
    String NETZKRONEHD_NAME = "NetzkroneHD";

    void sendMessage(Component message);
    void performCommand(String command);
    void showTitle(Title title);
    void resetTitle();

    boolean hasPermission(String permission);
    boolean isConsole();

    UUID getUniqueId();
    String getName();


}
