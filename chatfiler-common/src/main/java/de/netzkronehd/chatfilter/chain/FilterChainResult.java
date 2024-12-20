package de.netzkronehd.chatfilter.chain;

import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class FilterChainResult {

    /**
     * The results of the filter chain.
     * The results are ordered by the priority of the filter processors.
     */
    private final List<FilterProcessorResult> results;
    /**
     * Whether the message is allowed by all filter processors.
     */
    private final boolean allowed;
    /**
     * Whether the message is filtered by at least one filter processor.
     */
    private final boolean filtered;
    /**
     * Whether the message is blocked by at least one filter processor.
     */
    private final boolean blocked;

    public FilterChainResult(List<FilterProcessorResult> results) {
        this.results = Collections.unmodifiableList(results);
        this.allowed = results.stream().allMatch(FilterProcessorResult::isAllowed);
        this.filtered = results.stream().anyMatch(FilterProcessorResult::isFiltered);
        this.blocked = results.stream().anyMatch(FilterProcessorResult::isBlocked);
    }

    /**
     * Returns the last result of the filter chain.
     * @return the last result of the filter chain
     */
    public FilterProcessorResult getLastResult() {
        return results.get(results.size() - 1);
    }

    /**
     * Returns the first result of the filter chain.
     * @return the first result of the filter chain
     */
    public FilterProcessorResult getFirstResult() {
        return results.get(0);
    }

    public Optional<String> getReason() {
        return results.stream()
                .filter(FilterProcessorResult::isBlocked)
                .map(FilterProcessorResult::reason)
                .findFirst();
    }

    /**
     * Returns the first filtered message of the filter chain.
     * @return the first filtered message of the filter chain
     */
    public Optional<String> getFilteredMessage() {
        return results.stream()
                .filter(FilterProcessorResult::isFiltered)
                .map(FilterProcessorResult::filteredMessage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }


}
