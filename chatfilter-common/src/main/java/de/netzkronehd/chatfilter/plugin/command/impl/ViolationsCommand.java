package de.netzkronehd.chatfilter.plugin.command.impl;

import de.netzkronehd.chatfilter.database.model.UuidAndName;
import de.netzkronehd.chatfilter.player.ChatFilterPlayer;
import de.netzkronehd.chatfilter.plugin.FilterPlugin;
import de.netzkronehd.chatfilter.plugin.command.FilterCommand;
import de.netzkronehd.chatfilter.processor.FilterProcessor;
import de.netzkronehd.chatfilter.violation.FilterViolation;
import org.apache.commons.cli.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static de.netzkronehd.chatfilter.locale.Messages.*;
import static org.apache.commons.cli.Option.builder;

public class ViolationsCommand implements FilterCommand {

    private final FilterPlugin filterPlugin;
    private final CommandLineParser parser;
    private final Options options;
    private final SimpleDateFormat dateFormat;


    public ViolationsCommand(FilterPlugin filterPlugin) {
        this.filterPlugin = filterPlugin;
        this.parser = new DefaultParser();
        this.options = new Options()
                .addOption(builder().option("f").longOpt("from").hasArg().optionalArg(true).build())
                .addOption(builder().option("t").longOpt("to").hasArg().optionalArg(true).build())
                .addOption(builder().option("n").longOpt("filterName").hasArg().optionalArg(true).build());
        this.dateFormat = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
    }

    @Override
    public void execute(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer)) {
            NO_PERMISSION.send(chatFilterPlayer.getSender());
            return;
        }
        if(args.length < 1) {
            VIOLATIONS_USAGE.send(chatFilterPlayer.getSender());
            return;
        }

        try {
            final String playerName = args[1];
            final CommandLine parsed = parser.parse(options, args);
            final String filterName = parsed.getOptionValue("n");
            final String from = parsed.getOptionValue("f");
            final String to = parsed.getOptionValue("t");
            final long fromTime = (from == null) ? 0 : dateFormat.parse(from).getTime();
            final long toTime = (to == null) ? System.currentTimeMillis() : dateFormat.parse(to).getTime();
            filterPlugin.runAsync(() -> {
                try {
                    final UuidAndName uuid = filterPlugin.getDatabase().getUuid(playerName).orElse(null);
                    if (uuid == null) {
                        PLAYER_NOT_FOUND.send(chatFilterPlayer.getSender(), playerName);
                        return;
                    }
                    if (args[0].equalsIgnoreCase("show")) {
                        handleShow(chatFilterPlayer, uuid, filterName, fromTime, toTime);
                    } else if (args[0].equalsIgnoreCase("clear")) {
                        handleClear(chatFilterPlayer, uuid, filterName, fromTime, toTime);
                    } else {
                        VIOLATIONS_USAGE.send(chatFilterPlayer.getSender());
                    }

                } catch (SQLException e) {
                    ERROR.send(chatFilterPlayer.getSender(), e);
                    throw new RuntimeException(e);
                }
            });
        } catch (ParseException | java.text.ParseException e) {
            VIOLATIONS_USAGE.send(chatFilterPlayer.getSender());
        }
    }

    private void handleClear(ChatFilterPlayer player, UuidAndName uuidAndName, String filterName, long fromTime, long toTime) throws SQLException {
        final int violations;
        if (filterName == null) {
            violations = filterPlugin.getDatabase().deleteViolations(uuidAndName.uuid(), fromTime, toTime);
        } else {
            violations = filterPlugin.getDatabase().deleteViolations(uuidAndName.uuid(), filterName, fromTime, toTime);
        }
        CLEARED.send(player.getSender(), uuidAndName.name(), violations);
    }

    private void handleShow(ChatFilterPlayer player, UuidAndName uuidAndName, String filterName, long fromTime, long toTime) throws SQLException {
        final List<FilterViolation> violations;
        if (filterName == null) {
            violations = filterPlugin.getDatabase().listViolations(uuidAndName.uuid(), fromTime, toTime);
        } else {
            violations = filterPlugin.getDatabase().listViolations(uuidAndName.uuid(), filterName, fromTime, toTime);
        }
        VIOLATIONS.send(player.getSender(), violations, uuidAndName.name());
    }

    @Override
    public List<String> tabComplete(ChatFilterPlayer chatFilterPlayer, String[] args) {
        if(!hasPermission(chatFilterPlayer) || args.length < 1) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return List.of("show", "clear");
        }
        if (args.length == 2) {
            final String prefix = args[1].toLowerCase();

            return filterPlugin.getPlayers().stream()
                    .map(player -> player.getSender().getName())
                    .filter(name -> name.toLowerCase().startsWith(prefix)).toList();
        }
        final List<String> tabs = new ArrayList<>();
        final String lastArg = args[args.length - 2].toLowerCase();
        switch (lastArg) {
            case "-f", "-t" -> tabs.add(dateFormat.format(System.currentTimeMillis()));
            case "-n" -> {
                final String prefix = args[args.length - 1].toLowerCase();
                tabs.addAll(filterPlugin.getFilterChain().getProcessors().stream()
                    .map(FilterProcessor::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .toList());
            }
            default -> {
                tabs.add("-f");
                tabs.add("-t");
                tabs.add("-n");
            }
        }
        return tabs;

    }

    @Override
    public String getName() {
        return "violations";
    }

}
