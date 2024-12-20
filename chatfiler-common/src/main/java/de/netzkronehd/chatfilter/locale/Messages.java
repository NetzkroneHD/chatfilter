package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.violation.FilterViolation;
import de.netzkronehd.translation.args.Args;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.text.SimpleDateFormat;
import java.util.Date;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface Messages {

    SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    TextComponent FULL_STOP = Component.text('.');

    Component PREFIX_COMPONENT = text()
            .color(GRAY)
            .append(text('['))
            .append(text()
                    .decoration(BOLD, true)
                    .append(text('N', AQUA))
                    .append(text('C', BLUE))
                    .append(text('F', BLUE))
            )
            .append(text(']'))
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
        if(state.isAllowed()) return Component.text("ALLOWED", NamedTextColor.GREEN);
        if(state.isFiltered()) return Component.text("FILTERED", GOLD);
        if(state.isBlocked()) return Component.text("BLOCKED", NamedTextColor.RED);
        return Component.text("UNKNOWN", LIGHT_PURPLE);
    }



    Args.Args0 NO_PERMISSION = () -> prefixed(translatable()
            // "&cYou do not have permission to use this command."
            .key("chatfilter.command.no-permission")
            .color(RED)
            .append(FULL_STOP)
    );

    Args.Args1<FilterViolation> FILTER_VIOLATION = (violation) -> prefixed(translatable()
            // "&7Violation: &e%name% &7(%filter%) &7- %state% &7- (%time%) %message%"
            .key("chatfilter.command.filter-violation")
            .color(GRAY)
            .arguments(
                    text(violation.name()).color(YELLOW),
                    text(violation.filterName()).color(DARK_AQUA),
                    formatMessageState(violation.state()),
                    formatTime(violation.messageTime()).color(GRAY),
                    text(violation.message()).color(GRAY)
            ).append(FULL_STOP)
    );

}
