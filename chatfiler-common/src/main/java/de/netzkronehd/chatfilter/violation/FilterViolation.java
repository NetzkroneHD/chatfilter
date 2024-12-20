package de.netzkronehd.chatfilter.violation;

import de.netzkronehd.chatfilter.message.MessageState;

import java.util.Objects;
import java.util.UUID;

public record FilterViolation(UUID uniqueId, String name, String filterName, MessageState state, String message, long messageTime) {

    public FilterViolation {
        Objects.requireNonNull(uniqueId, "uniqueId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(filterName, "filterName");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(message, "message");
    }

}
