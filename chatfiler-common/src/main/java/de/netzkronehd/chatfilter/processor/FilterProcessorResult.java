package de.netzkronehd.chatfilter.processor;

import de.netzkronehd.chatfilter.message.MessageState;

import java.util.Optional;

/**
 * Represents the result of a {@link FilterProcessor} processing a message.
 *
 */
public record FilterProcessorResult(String message, Optional<String> filteredMessage, MessageState state, FilterProcessor processor, String reason) {

    /**
     * Creates a new {@link FilterProcessorResult}.
     * If the state is {@link MessageState#FILTERED} the filtered message must be present.
     * @param message the original message
     * @param filteredMessage the filtered message or an empty optional if the message was allowed
     * @param state the state of the message
     * @param processor the processor that processed the message
     * @param reason the reason why the message was blocked, this message will be sent to the player
     */
    public FilterProcessorResult {
        if (state.isFiltered() && filteredMessage.isEmpty()) {
            throw new IllegalArgumentException("Filtered message must be present if state is FILTERED");
        }
    }

    /**
     * Creates a new {@link FilterProcessorResult} with the state {@link MessageState#ALLOWED}.
     * @param message the original message
     * @param processor the processor that processed the message
     * @return a new {@link FilterProcessorResult} with the state {@link MessageState#ALLOWED}
     */
    public static FilterProcessorResult allowed(String message, FilterProcessor processor, String reason) {
        return new FilterProcessorResult(message, Optional.empty(), MessageState.ALLOWED, processor, reason);
    }

    /**
     * Creates a new {@link FilterProcessorResult} with the state {@link MessageState#FILTERED}.
     * @param message the original message
     * @param transformedMessage the transformed message
     * @param processor the processor that processed the message
     * @return a new {@link FilterProcessorResult} with the state {@link MessageState#FILTERED}
     */
    public static FilterProcessorResult filtered(String message, String transformedMessage, FilterProcessor processor, String reason) {
        return new FilterProcessorResult(message, Optional.of(transformedMessage), MessageState.FILTERED, processor, reason);
    }

    /**
     * Creates a new {@link FilterProcessorResult} with the state {@link MessageState#BLOCKED}.
     * @param message the original message
     * @param processor the processor that processed the message
     * @return a new {@link FilterProcessorResult} with the state {@link MessageState#BLOCKED}
     */
    public static FilterProcessorResult blocked(String message, FilterProcessor processor, String reason) {
        return new FilterProcessorResult(message, Optional.empty(), MessageState.BLOCKED, processor, reason);
    }

    /**
     * Creates a new {@link FilterProcessorResult} with the state {@link MessageState#BLOCKED}.
     * @param message the original message
     * @param filteredMessage may be the word that was blocked
     * @param processor the processor that processed the message
     * @return a new {@link FilterProcessorResult} with the state {@link MessageState#BLOCKED}
     */
    public static FilterProcessorResult blocked(String message, String filteredMessage, FilterProcessor processor, String reason) {
        return new FilterProcessorResult(message, Optional.of(filteredMessage), MessageState.BLOCKED, processor, reason);
    }

    public boolean isAllowed() {
        return state.isAllowed();
    }

    public boolean isFiltered() {
        return state.isFiltered();
    }

    public boolean isBlocked() {
        return state.isBlocked();
    }

}
