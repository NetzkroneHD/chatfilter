package de.netzkronehd.chatfilter.processor;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import org.jetbrains.annotations.Nullable;

public interface FilterProcessor {

    /**
     * Processes the message and returns the result of the processing.
     * The result can be allowed, blocked or modified.
     * @see FilterProcessorResult
     * @param player the player who sent the message
     * @param previousResult the result of the previous processor or null if this is the first processor
     * @param message the message to process
     * @return the result of the processing
     */
    FilterProcessorResult process(ChatFilterPlayer player, @Nullable FilterProcessorResult previousResult, String message);

    /**
     * The priority of the filter processor.
     * The higher the priority, the earlier the processor will be executed.
     * @return the priority of the filter processor
     */
    int getPriority();

    /**
     * The name of the filter processor.
     * @return the name of the filter processor
     */
    String getName();

}
