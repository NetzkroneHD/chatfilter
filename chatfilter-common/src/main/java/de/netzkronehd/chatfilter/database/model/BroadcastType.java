package de.netzkronehd.chatfilter.database.model;

import de.netzkronehd.chatfilter.player.ReceiveBroadcastType;

import static de.netzkronehd.chatfilter.player.ReceiveBroadcastType.DEFAULT;

public record BroadcastType(ReceiveBroadcastType filtered, ReceiveBroadcastType blocked) {

    public static BroadcastType of(ReceiveBroadcastType filtered, ReceiveBroadcastType blocked) {
        return new BroadcastType((filtered == null ? DEFAULT:filtered), (blocked == null ? DEFAULT:blocked));
    }

}
