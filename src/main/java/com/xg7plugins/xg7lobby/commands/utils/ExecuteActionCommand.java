package com.xg7plugins.xg7lobby.commands.utils;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionType;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "execute",
        permission = "xg7lobby.command.execute",
        description = "Execute an action",
        syntax = "/7lexecute \"[ACTIONNAME] [args...]\"",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.CRAFTING_TABLE
)
public class ExecuteActionCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() == 0) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String actionArgs = args.toString();

        ActionsProcessor.process(Collections.singletonList(actionArgs), (Player) sender);

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? Arrays.stream(ActionType.values()).map(type -> "[" + type.name() + "] ").collect(Collectors.toList()) : Collections.emptyList();
    }

}