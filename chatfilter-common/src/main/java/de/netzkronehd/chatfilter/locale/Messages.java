package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.chatfilter.chain.FilterChainResult;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.processor.FilterProcessorResult;
import de.netzkronehd.chatfilter.violation.FilterViolation;
import de.netzkronehd.translation.args.Args;
import de.netzkronehd.translation.manager.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static de.netzkronehd.translation.Message.formatBoolean;
import static de.netzkronehd.translation.Message.formatContext;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface Messages {

    TranslationManager TRANSLATION_MANAGER = new TranslationManager("netzchatfilter", "main");

    SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    TextComponent FULL_STOP = text('.');
    TextComponent ARROW_RIGHT = text("»");
    TextComponent ARROW_LEFT = text("«");

    Component PREFIX_COMPONENT = text()
            .color(GRAY)
            .append(text('[').color(DARK_GRAY))
            .append(text()
                    .decoration(BOLD, true)
                    .append(text('N', AQUA))
                    .append(text('C', BLUE))
                    .append(text('F', BLUE))
            )
            .append(text(']').color(DARK_GRAY))
            .append(space())
            .append(ARROW_RIGHT)
            .build();

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(PREFIX_COMPONENT)
                .append(space())
                .append(component)
                .build();
    }

    static TextComponent formatTime(long time) {
        return text(DATE_TIME_FORMATTER.format(new Date(time)));
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



    Args.Args0 NO_PERMISSION = () -> prefixed(translatable()
            // "&cYou do not have permission to use this command."
            .key("chatfilter.command.no-permission")
            .color(RED)
            .append(FULL_STOP)
    );

    Args.Args0 RELOADING = () -> prefixed(translatable()
            // "&7Reloading..."
            .key("chatfilter.command.reloading")
            .color(GRAY)
    );

    Args.Args1<Long> RELOAD_COMPLETE = (time) -> prefixed(translatable()
            // "&aReloaded after &e{0}ms"
            .key("chatfilter.command.reload-complete")
            .color(GREEN)
            .arguments(text(time).color(YELLOW))
            .append(FULL_STOP)
    );

    Args.Args0 PARSE_USAGE = () -> prefixed(translatable()
            // "&cUsage: &e/chatfilter parse <filter> <message>"
            .key("chatfilter.command.parse.usage")
            .color(RED)
    );

    Args.Args0 BASE_USAGE = () -> prefixed(translatable()
            // "&eparse <filter> <message>&8 -&7 Parses a message through the filter chain\n"
            // "&eviolations <player> [options]&8 -&7 Lists the violations of a player\n"
            // "&ereload&8 -&7 Reloads the plugin\"
            // "&cUsage: &e/chatfilter <subcommand> [options]"
            .key("chatfilter.command.base.usage")
            .color(RED)
    );

    Args.Args0 VIOLATIONS_USAGE = () -> prefixed(translatable()
            // &cOptions: &e-f <from> &e-t <to> &e-n <filterName>\n
            // "&cUsage: &e/chatfilter violations <player> [options]
            .key("chatfilter.command.violations.usage")
            .color(RED)
    );

    Args.Args1<String> BLOCKED = (reason) -> prefixed(translatable()
            // "&cMessage blocked: &e{0}"
            .key("chatfilter.blocked")
            .color(RED)
            .arguments(text(reason).color(YELLOW))
    );

    Args.Args1<Exception> ERROR = (ex) -> prefixed(translatable()
            // "&cAn error occurred: &e{0}"
            .key("chatfilter.error")
            .color(RED)
            .arguments(text(ex.getMessage()).color(YELLOW))
    );

    Args.Args1<String> PLAYER_NOT_FOUND = (player) -> prefixed(translatable()
            // "&cPlayer &e{0}&c not found."
            .key("chatfilter.player-not-found")
            .color(RED)
            .arguments(text(player).color(YELLOW))
            .append(FULL_STOP)
    );

    Args.Args1<String> FILTER_NOT_FOUND = (filter) -> prefixed(translatable()
            // "&cFilter &e{0}&c not found."
            .key("chatfilter.command.filter-not-found")
            .color(RED)
            .arguments(text(filter).color(YELLOW))
            .append(FULL_STOP)
    );

    Args.Args1<FilterChainResult> PARSE_RESULT = (result) -> prefixed(translatable()
            // "&3allowed&7: {0} &3filtered&7: {1} &3blocked&7: {2}\n
            // &3Filters&7:\n
            // {3}"
            .key("chatfilter.command.parse.result")
            .color(GRAY)
            .arguments(
                    formatBoolean(result.isAllowed()),
                    formatBoolean(result.isFiltered()),
                    formatBoolean(result.isBlocked()),
                    newline().append(formatProcessorResult(result.getResults()))
            ).append(FULL_STOP)
    );

    Args.Args2<FilterViolation, String> FILTER_VIOLATION = (violation, playerName) -> prefixed(translatable()
            // "&3{0}&7: &e{0} &7({1}) &7- {2} &7- ({3}) {4}"
            // &31&7: &eNetzkroneHD &7(MaxSimilarityFilter) &7- &cBLOCKED &7- (12:00:00 05.06.2002) Hello World"
            .key("chatfilter.command.violations.violation")
            .color(GRAY)
            .arguments(
                    text(violation.id()).color(DARK_AQUA),
                    text(playerName).color(YELLOW),
                    text(violation.filterName()).color(DARK_AQUA),
                    formatMessageState(violation.state()),
                    formatTime(violation.messageTime()).color(GRAY),
                    text(violation.message()).color(GRAY)
            )
    );

}
