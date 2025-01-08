package de.netzkronehd.chatfilter.player;

public enum ReceiveBroadcastType {

    /**
     * The default broadcast receiver type.
     * The player will receive the broadcast if it is enabled in the config.
     */
    DEFAULT,
    /**
     * The player will not receive the broadcast.
     */
    HIDE,
    /**
     * The player will receive the broadcast even if it is disabled in the config.
     */
    SHOW;

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isHide() {
        return this == HIDE;
    }

    public boolean isShow() {
        return this == SHOW;
    }

    public boolean canReceiveBroadcast(boolean broadcastEnabled) {
        return broadcastEnabled || isShow();
    }

}
