package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.player.ReceiveBroadcastType;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import de.netzkronehd.chatfilter.violation.FilterViolation;
import de.netzkronehd.translation.args.Args;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static de.netzkronehd.translation.MessageUtils.formatBoolean;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface Messages {

    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

    TextComponent UNKNOWN = text("UNKNOWN", LIGHT_PURPLE);

    static TextComponent prefixed(ComponentLike component) {
        return empty()
                .append(translatable().key("chatfilter.prefix"))
                .append(space())
                .append(component);
    }

    static Component prefix() {
        return translatable().key("chatfilter.prefix").build();
    }

    static TextComponent formatTime(long time) {
        return text(DATE_FORMAT.format(new Date(time)));
    }

    static TextComponent formatReceiverType(ReceiveBroadcastType type) {
        if(type == ReceiveBroadcastType.DEFAULT) return text(type.name().toLowerCase(), GRAY);
        if(type == ReceiveBroadcastType.HIDE) return text(type.name().toLowerCase(), DARK_GRAY);
        if(type == ReceiveBroadcastType.SHOW) return text(type.name().toLowerCase(), GREEN);
        return UNKNOWN;
    }

    static TextComponent formatMessageState(MessageState state) {
        if(state.isAllowed()) return text("ALLOWED", GREEN);
        if(state.isFiltered()) return text("FILTERED", GOLD);
        if(state.isBlocked()) return text("BLOCKED", RED);
        return UNKNOWN;
    }

    static Component formatProcessorResult(FilterProcessorResult result) {
        return translatable().key("chatfilter.processor-result")
                .arguments(
                        prefix(),
                        text(result.filteredMessage().orElse("null")),
                        formatMessageState(result.state()),
                        text(result.processor().getName()),
                        text(result.reason()))
                .build();
    }

    static TextComponent formatProcessorResult(List<FilterProcessorResult> result) {
        final TextComponent.Builder text = text();
        for (FilterProcessorResult processorResult : result) {
            text.append(formatProcessorResult(processorResult).append(newline()));
        }
        return text.build();
    }


    Args.Args0 COMMAND_NO_PERMISSION = () -> translatable()
            .key("chatfilter.no-permission")
            .arguments(prefix())
            .build();

    Args.Args0 COMMAND_RELOADING = () -> translatable()
            .key("chatfilter.command.reload.reloading")
            .arguments(prefix())
            .build();

    Args.Args0 COMMAND_BROADCAST_USAGE = () -> translatable()
            .key("chatfilter.command.broadcast.usage")
            .arguments(prefix())
            .build();

    Args.Args1<Long> COMMAND_RELOAD_COMPLETE = (time) -> translatable()
            .key("chatfilter.command.reload.complete")
            .arguments(prefix(), text(time))
            .build();

    Args.Args0 COMMAND_PARSE_USAGE = () -> translatable()
            .key("chatfilter.command.parse.usage")
            .arguments(prefix())
            .build();

    Args.Args0 COMMAND_BASE_USAGE = () -> translatable()
            .key("chatfilter.command.base.usage")
            .arguments(prefix())
            .build();

    Args.Args0 COMMAND_VIOLATIONS_USAGE = () -> translatable()
            .key("chatfilter.command.violations.usage")
            .arguments(prefix())
            .build();

    Args.Args1<String> BLOCKED = (reason) -> translatable()
            .key("chatfilter.blocked")
            .arguments(prefix(), text(reason))
            .build();

    Args.Args1<Exception> ERROR = (ex) -> translatable()
            .key("chatfilter.error")
            .arguments(prefix(), text(ex.getMessage()))
            .build();

    Args.Args1<String> PLAYER_NOT_FOUND = (player) -> translatable()
            .key("chatfilter.player-not-found")
            .arguments(prefix(), text(player))
            .build();

    Args.Args1<String> COMMAND_FILTER_NOT_FOUND = (filter) -> translatable()
            .key("chatfilter.command.filter-not-found")
            .arguments(prefix(), text(filter))
            .build();

    Args.Args1<FilterChainResult> COMMAND_PARSE_RESULT = (result) -> translatable()
            .key("chatfilter.command.parse.result")
            .arguments(prefix(),
                    formatBoolean(result.isAllowed()),
                    formatBoolean(result.isFiltered()),
                    formatBoolean(result.isBlocked()),
                    formatProcessorResult(result.getResults()))
            .build();

    Args.Args2<String, ReceiveBroadcastType> COMMAND_BROADCAST_SUCCESS = (broadcastType, receiveBroadcastType) -> translatable()
            .key("chatfilter.command.broadcast.success")
            .arguments(prefix(), text(broadcastType), formatReceiverType(receiveBroadcastType))
            .build();

    Args.Args2<String, Integer> COMMAND_CLEARED = (player, count) -> translatable()
            .key("chatfilter.command.violations.cleared")
            .arguments(prefix(), text(player), text(count))
            .build();

    Args.Args4<String, String, String, String> BROADCAST_BLOCKED = (playerName, filter, reason, message) -> translatable()
            .key("chatfilter.broadcast.blocked")
            .arguments(prefix(), text(playerName), text(filter), text(reason), text(message))
            .build();

    Args.Args4<String, String, String, String> BROADCAST_FILTERED = (playerName, filter, reason, message) -> translatable()
            .key("chatfilter.broadcast.filtered")
            .arguments(prefix(), text(playerName), text(filter), text(reason), text(message))
            .build();

    Args.Args2<FilterViolation, String> COMMAND_FILTER_VIOLATION = (violation, playerName) -> translatable()
            .key("chatfilter.command.violations.violation")
            .arguments(prefix(),
                    text(violation.id()),
                    text(playerName),
                    text(violation.filterName()),
                    formatMessageState(violation.state()),
                    formatTime(violation.messageTime()),
                    text(violation.message()))
            .build();

    Args.Args4<List<FilterViolation>, String, Integer, Integer> COMMAND_VIOLATIONS = (violations, playerName, currentPage, maxPage) -> {
        final TextComponent.Builder builder = text();
        violations.forEach(violation -> builder.append(COMMAND_FILTER_VIOLATION.build(violation, playerName)).append(newline()));

        return translatable()
                .key("chatfilter.command.violations.violations")
                .arguments(prefix(),
                        text(playerName),
                        text(currentPage),
                        text(maxPage),
                        builder)
                .build();
    };

    private static Component deserialize(String message, TagResolver... tagResolvers) {
        return miniMessage().deserialize(message, tagResolvers);
    }

}
