package de.netzkronehd.chatfilter.chain;

import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilterChain {

    private final List<FilterProcessor> processors;

    public FilterChain() {
        this.processors = new ArrayList<>();
    }

    /**
     * Processes the message through all filter processors.
     * @param player the player who sent the message
     * @param message the message to process
     * @param stopOnBlock whether to stop processing when a processor blocks the message
     * @return the result of the processing
     */
    public FilterChainResult process(ChatFilterPlayer player, String message, boolean stopOnBlock) {
        final List<FilterProcessorResult> results = new ArrayList<>();
        FilterProcessorResult previousResult = null;
        for (FilterProcessor processor : processors) {
            final FilterProcessorResult result = processor.process(player, previousResult, message);
            results.add(result);
            if (stopOnBlock && result.isBlocked()) {
                break;
            }
            previousResult = result;
        }
        return new FilterChainResult(results);
    }

    /**
     * Adds a filter processor to the chain.
     * @param processor the filter processor to add
     */
    public void addProcessor(FilterProcessor processor) {
        this.processors.add(processor);
        sortProcessors();
    }

    /**
     * Removes a filter processor from the chain.
     * @param processor the filter processor to remove
     */
    public void removeProcessor(FilterProcessor processor) {
        this.processors.remove(processor);
        sortProcessors();
    }

    /**
     * Returns an unmodifiable list of filter processors.
     * @return an unmodifiable list of filter processors
     */
    public List<FilterProcessor> getProcessors() {
        return Collections.unmodifiableList(processors);
    }

    /**
     * Sorts the filter processors by priority.
     */
    private void sortProcessors() {
        processors.sort(Comparator.comparingInt(FilterProcessor::getPriority).reversed());
    }

}
