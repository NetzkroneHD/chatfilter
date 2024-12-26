package de.netzkronehd.chatfilter.chain;

import de.netzkronehd.chatfilter.exception.FilterNotFoundException;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;

import java.util.*;

public class FilterChain {

    private final List<FilterProcessor> processors;

    public FilterChain() {
        this.processors = new ArrayList<>();
    }


    /**
     * Runs
     * @see #process(ChatFilterPlayer, String, boolean)
     * with stopOnBlock set to true.
     */
    public FilterChainResult process(ChatFilterPlayer player, String message) {
        return process(player, message, true);
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
     * Runs
     * @see #process(ChatFilterPlayer, String, String, boolean)
     * with ignoreCase set to true.
     */
    public FilterChainResult process(ChatFilterPlayer player, String message, String processorName) throws FilterNotFoundException {
        return process(player, message, processorName, true);
    }

    /**
     * Processes the message through a specific filter processor. The processor is identified by its name.
     * @param player the player who sent the message
     * @param message the message to process
     * @param processorName the name of the filter processor
     * @param ignoreCase whether to ignore the case of the processor name
     * @return the result of the processing
     */
    public FilterChainResult process(ChatFilterPlayer player, String message, String processorName, boolean ignoreCase) throws FilterNotFoundException {
        final FilterProcessorResult result = findProcessor(processorName, ignoreCase)
                .orElseThrow(() -> new FilterNotFoundException(processorName))
                .process(player, null, message);
        return new FilterChainResult(Collections.singletonList(result));
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
     * Finds a filter processor by name.
     * @param name the name of the filter processor
     * @param ignoreCase whether to ignore the case of the name
     * @return an optional containing the filter processor if found, otherwise an empty optional
     */
    public Optional<FilterProcessor> findProcessor(String name, boolean ignoreCase) {
        return processors.stream()
                .filter(processor -> ignoreCase ? processor.getName().equalsIgnoreCase(name) : processor.getName().equals(name))
                .findFirst();
    }

    /**
     * Sorts the filter processors by priority.
     */
    private void sortProcessors() {
        processors.sort(Comparator.comparingInt(FilterProcessor::getPriority).reversed());
    }

}
