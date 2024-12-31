package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import de.netzkronehd.chatfilter.violation.FilterViolation;
import de.netzkronehd.translation.args.Args;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Date;
import java.util.List;

import static de.netzkronehd.chatfilter.locale.MessagesProvider.translate;
import static de.netzkronehd.translation.Message.formatContext;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Formatter.date;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

public interface Messages {

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(deserialize(translate("chatfilter.prefix")))
                .append(space())
                .append(component)
                .build();
    }

    static TextComponent formatMessageState(MessageState state) {
        if(state.isAllowed()) return text("ALLOWED", NamedTextColor.GREEN);
        if(state.isFiltered()) return text("FILTERED", GOLD);
        if(state.isBlocked()) return text("BLOCKED", NamedTextColor.RED);
        return text("UNKNOWN", LIGHT_PURPLE);
    }

    static TextComponent formatProcessorResult(FilterProcessorResult result) {
        return text("- ").color(GRAY)
                .append(formatContext("filteredMessage", result.filteredMessage().orElse("null")))
                .append(formatContext("state", formatMessageState(result.state())))
                .append(formatContext("processor", result.processor().getName()))
                .append(formatContext("reason", result.reason()));
    }

    static TextComponent formatProcessorResult(List<FilterProcessorResult> result) {
        final TextComponent.Builder text = text();
        for (FilterProcessorResult processorResult : result) {
            text.append(formatProcessorResult(processorResult).append(newline()));
        }
        return text.build();
    }



    Args.Args0 NO_PERMISSION = () -> prefixed(
            // "&cYou do not have permission to use this command."
            deserialize(translate("chatfilter.command.no-permission"))
            .color(RED)
    );

    Args.Args0 RELOADING = () -> prefixed(
            // "&7Reloading..."
            deserialize(translate("chatfilter.command.reloading"))
            .color(GRAY)
    );

    Args.Args1<Long> RELOAD_COMPLETE = (time) -> prefixed(
            // "&aReloaded after &e<time>ms"
            deserialize(
                    translate("chatfilter.command.reload-complete"),
                    component("time", text(time).color(YELLOW))
            )
            .color(GREEN)
    );

    Args.Args0 PARSE_USAGE = () -> prefixed(
            // "&cUsage: &e/chatfilter parse <filter> <message>"
            deserialize(translate("chatfilter.command.parse.usage"))
            .color(RED)
    );

    Args.Args0 BASE_USAGE = () -> prefixed(
            // "&eparse <filter> <message>&8 -&7 Parses a message through the filter chain\n"
            // "&eviolations <player> [options]&8 -&7 Lists the violations of a player\n"
            // "&ereload&8 -&7 Reloads the plugin\"
            // "&cUsage: &e/chatfilter <subcommand> [options]"
            deserialize(translate("chatfilter.command.base.usage"))
            .color(RED)
    );

    Args.Args0 VIOLATIONS_USAGE = () -> prefixed(
            // &cOptions: &e-f <from> &e-t <to> &e-n <filterName>\n
            // "&cUsage: &e/chatfilter violations <player> [options]
            deserialize(translate("chatfilter.command.violations.usage"))
            .color(RED)
    );

    Args.Args1<String> BLOCKED = (reason) -> prefixed(
            // "&cMessage blocked: &e{0}"
            deserialize(
                    translate("chatfilter.blocked"),
                    component("reason", text(reason).color(YELLOW))
            )
            .color(RED)
    );

    Args.Args1<Exception> ERROR = (ex) -> prefixed(
            // "&cAn error occurred: &e{0}"
            deserialize(
                    translate("chatfilter.error"),
                    component("error", text(ex.getMessage()).color(YELLOW))
            )
            .color(RED)
    );

    Args.Args1<String> PLAYER_NOT_FOUND = (player) -> prefixed(
            // "&cPlayer &e{0}&c not found."
            deserialize(
                    translate("chatfilter.player-not-found"),
                    component("player", text(player).color(YELLOW))
            )
            .color(RED)
    );

    Args.Args1<String> FILTER_NOT_FOUND = (filter) -> prefixed(
            // "&cFilter &e{0}&c not found."
            deserialize(
                    translate("chatfilter.command.filter-not-found"),
                    component("filter", text(filter).color(YELLOW))
            )
            .color(RED)
    );

    Args.Args1<FilterChainResult> PARSE_RESULT = (result) -> prefixed(
            // "&3allowed&7: {0} &3filtered&7: {1} &3blocked&7: {2}\n
            // &3Filters&7:\n
            // <filters>"
            deserialize(
                    translate("chatfilter.command.parse.result"),
                    component("allowed", text(result.isAllowed()).color(DARK_AQUA)),
                    component("filtered", text(result.isFiltered()).color(DARK_AQUA)),
                    component("blocked", text(result.isBlocked()).color(DARK_AQUA)),
                    component("filters", formatProcessorResult(result.getResults()))
            )
            .color(GRAY)
    );

    Args.Args2<FilterViolation, String> FILTER_VIOLATION = (violation, playerName) -> prefixed(
            // "&3<id>&7: &e<player> &7(<filter>) &7- <state> &7- (<date:'yyyy-MM-dd HH:mm:ss'>) <message>"
            // &31&7: &eNetzkroneHD &7(MaxSimilarityFilter) &7- &cBLOCKED &7- (12:00:00 05.06.2002) Hello World"
            deserialize(
                    translate("chatfilter.command.violations.violation"),
                    component("id", text(violation.id()).color(DARK_AQUA)),
                    component("player", text(playerName).color(YELLOW)),
                    component("filter", text(violation.filterName()).color(DARK_AQUA)),
                    component("state", formatMessageState(violation.state())),
                    date("date", new Date(violation.messageTime()).toInstant()),
                    component("message", text(violation.message()).color(GRAY))
            )
            .color(GRAY)
    );

    private static Component deserialize(String message, TagResolver... tagResolvers) {
        return miniMessage().deserialize(message, tagResolvers);
    }

}
