package de.netzkronehd.chatfilter.message;

/**
 * Represents the state of a message after being processed by a {@link de.netzkronehd.chatfilter.processor.FilterProcessor}.
 */
public enum MessageState {

    ALLOWED,
    FILTERED,
    BLOCKED;

    public boolean isAllowed() {
        return this == ALLOWED;
    }

    public boolean isFiltered() {
        return this == FILTERED;
    }

    public boolean isBlocked() {
        return this == BLOCKED;
    }

}
