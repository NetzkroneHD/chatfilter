package de.netzkronehd.chatfilter.locale;

import de.netzkronehd.translation.args.Args;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface Messages {

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

    Args.Args0 NO_PERMISSION = () -> prefixed(translatable()
            // "&cYou do not have permission to use this command."
            .key("chatfilter.no_permission")
            .color(RED)
            .append(FULL_STOP)
    );

}
