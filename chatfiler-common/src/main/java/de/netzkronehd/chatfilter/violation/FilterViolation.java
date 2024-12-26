package de.netzkronehd.chatfilter.violation;

import de.netzkronehd.chatfilter.message.MessageState;

import java.util.Objects;
import java.util.UUID;

public record FilterViolation(int id, UUID uniqueId, String filterName, MessageState state, String message, long messageTime) {

    public FilterViolation {
        Objects.requireNonNull(uniqueId, "uniqueId");
        Objects.requireNonNull(filterName, "filterName");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(message, "message");
    }

}
