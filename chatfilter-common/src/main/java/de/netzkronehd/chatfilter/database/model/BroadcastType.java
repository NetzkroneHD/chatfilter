package de.netzkronehd.chatfilter.database.model;

import de.netzkronehd.chatfilter.player.ReceiveBroadcastType;

public record BroadcastType(ReceiveBroadcastType filtered, ReceiveBroadcastType blocked) {

    public static BroadcastType of(ReceiveBroadcastType filtered, ReceiveBroadcastType blocked) {
        return new BroadcastType(filtered, blocked);
    }

}
