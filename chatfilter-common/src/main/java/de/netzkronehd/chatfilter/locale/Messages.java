package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import de.netzkronehd.chatfilter.translation.args.Args;
import de.netzkronehd.chatfilter.violation.FilterViolation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static de.netzkronehd.chatfilter.locale.MessagesProvider.translate;
import static de.netzkronehd.chatfilter.translation.Message.formatBoolean;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

public interface Messages {

    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(deserialize(translate("chatfilter.prefix")))
                .append(space())
                .append(component)
                .build();
    }

    static Component prefix() {
        return deserialize(translate("chatfilter.prefix"));
    }

    static TextComponent formatTime(long time) {
        return text(DATE_FORMAT.format(new Date(time)));
    }

    static TextComponent formatMessageState(MessageState state) {
        if(state.isAllowed()) return text("ALLOWED", NamedTextColor.GREEN);
        if(state.isFiltered()) return text("FILTERED", GOLD);
        if(state.isBlocked()) return text("BLOCKED", NamedTextColor.RED);
        return text("UNKNOWN", LIGHT_PURPLE);
    }

    static Component formatProcessorResult(FilterProcessorResult result) {
        return deserialize(
                translate("chatfilter.processor-result"),
                component("prefix", prefix()),
                component("filtered_message", text(result.filteredMessage().orElse("null"))),
                component("state", formatMessageState(result.state())),
                component("processor", text(result.processor().getName())),
                component("reason", text(result.reason()))
        );
    }

    static TextComponent formatProcessorResult(List<FilterProcessorResult> result) {
        final TextComponent.Builder text = text();
        for (FilterProcessorResult processorResult : result) {
            text.append(formatProcessorResult(processorResult).append(newline()));
        }
        return text.build();
    }



    Args.Args0 NO_PERMISSION = () ->
            // "&cYou do not have permission to use this command."
            deserialize(
                    translate("chatfilter.command.no-permission"),
                    component("prefix", prefix())
            )
            .color(RED);

    Args.Args0 RELOADING = () ->
            // "&7Reloading..."
            deserialize(
                    translate("chatfilter.command.reload.reloading"),
                    component("prefix", prefix())
            )
            .color(GRAY);

    Args.Args1<Long> RELOAD_COMPLETE = (time) ->
            // "&aReloaded after &e<time>ms"
            deserialize(
                    translate("chatfilter.command.reload.complete"),
                    component("prefix", prefix()),
                    component("time", text(time))
            )
            .color(GREEN);

    Args.Args0 PARSE_USAGE = () ->
            // "&cUsage: &e/chatfilter parse <filter> <message>"
            deserialize(
                    translate("chatfilter.command.parse.usage"),
                    component("prefix", prefix())
            )
            .color(RED);

    Args.Args0 BASE_USAGE = () ->
            // "&eparse <filter> <message>&8 -&7 Parses a message through the filter chain\n"
            // "&eviolations <player> [options]&8 -&7 Lists the violations of a player\n"
            // "&ereload&8 -&7 Reloads the plugin\"
            // "&cUsage: &e/chatfilter <subcommand> [options]"
            deserialize(
                    translate("chatfilter.command.base.usage"),
                    component("prefix", prefix())
            )
            .color(RED);

    Args.Args0 VIOLATIONS_USAGE = () ->
            // &cOptions: &e-f <from> &e-t <to> &e-n <filterName>\n
            // "&cUsage: &e/chatfilter violations <player> [options]
            deserialize(
                    translate("chatfilter.command.violations.usage"),
                    component("prefix", prefix())
            )
            .color(RED);

    Args.Args1<String> BLOCKED = (reason) ->
            // "&cMessage blocked: &e{0}"
            deserialize(
                    translate("chatfilter.blocked"),
                    component("prefix", prefix()),
                    component("reason", text(reason))
            )
            .color(RED);

    Args.Args1<Exception> ERROR = (ex) ->
            // "&cAn error occurred: &e{0}"
            deserialize(
                    translate("chatfilter.error"),
                    component("prefix", prefix()),
                    component("error", text(ex.getMessage()))
            )
            .color(RED);

    Args.Args1<String> PLAYER_NOT_FOUND = (player) ->
            // "&cPlayer &e{0}&c not found."
            deserialize(
                    translate("chatfilter.player-not-found"),
                    component("prefix", prefix()),
                    component("player", text(player))
            )
            .color(RED);

    Args.Args1<String> FILTER_NOT_FOUND = (filter) ->
            // "&cFilter &e{0}&c not found."
            deserialize(
                    translate("chatfilter.command.filter-not-found"),
                    component("prefix", prefix()),
                    component("filter", text(filter))
            )
            .color(RED);

    Args.Args1<FilterChainResult> PARSE_RESULT = (result) ->
            // "&3allowed&7: {0} &3filtered&7: {1} &3blocked&7: {2}\n
            // &3Filters&7:\n
            // <filters>"
            deserialize(
                    translate("chatfilter.command.parse.result"),
                    component("prefix", prefix()),
                    component("allowed", formatBoolean(result.isAllowed())),
                    component("filtered", formatBoolean(result.isFiltered())),
                    component("blocked", formatBoolean(result.isBlocked())),
                    component("filters", formatProcessorResult(result.getResults()))
            )
            .color(GRAY);

    Args.Args2<String, Integer> CLEARED = (player, count) ->
            // "&7Cleared &e{0}&7 violations."
            deserialize(
                    translate("chatfilter.command.violations.cleared"),
                    component("prefix", prefix()),
                    component("player", text(player)),
                    component("count", text(count))
            )
            .color(GRAY);

    Args.Args4<String, String, String, String> BROADCAST_BLOCKED = (playerName, filter, reason, message) ->
            //
            deserialize(
                    translate("chatfilter.broadcast.blocked"),
                    component("prefix", prefix()),
                    component("player", text(playerName)),
                    component("filter", text(filter)),
                    component("reason", text(reason)),
                    component("message", text(message))
            );

    Args.Args4<String, String, String, String> BROADCAST_FILTERED = (playerName, filter, reason, message) ->
            //
            deserialize(
                    translate("chatfilter.broadcast.filtered"),
                    component("prefix", prefix()),
                    component("player", text(playerName)),
                    component("filter", text(filter)),
                    component("reason", text(reason)),
                    component("message", text(message))
            );

    Args.Args2<FilterViolation, String> FILTER_VIOLATION = (violation, playerName) ->
            // "&3<id>&7: &e<player> &7(<filter>) &7- <state> &7- (<datetime:'yyyy-MM-dd HH:mm:ss'>) <message>"
            // &31&7: &eNetzkroneHD &7(MaxSimilarityFilter) &7- &cBLOCKED &7- (12:00:00 05.06.2002) Hello World"
            deserialize(
                    translate("chatfilter.command.violations.violation"),
                    component("prefix", prefix()),
                    component("id", text(violation.id())),
                    component("player", text(playerName)),
                    component("filter", text(violation.filterName())),
                    component("state", formatMessageState(violation.state())),
                    component("datetime", formatTime(violation.messageTime())),
                    component("message", text(violation.message()))
            )
            .color(GRAY);

    Args.Args4<List<FilterViolation>, String, Integer, Integer> VIOLATIONS = (violations, playerName, currentPage, maxPage) -> {
        final TextComponent.Builder builder = text();
        violations.forEach(violation -> builder.append(FILTER_VIOLATION.build(violation, playerName)).append(newline()));

        return deserialize(
                translate("chatfilter.command.violations.violations"),
                component("prefix", prefix()),
                component("player", text(playerName)),
                component("from", text(currentPage)),
                component("to", text(maxPage)),
                component("violations", builder)
        );
    };

    private static Component deserialize(String message, TagResolver... tagResolvers) {
        return miniMessage().deserialize(message, tagResolvers);
    }

}
