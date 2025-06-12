package com.xg7plugins.xg7lobby.commands.utils;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.aciton.ActionType;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "execute",
        permission = "xg7lobby.command.execute",
        description = "Execute an action",
        syntax = "/7lexecute \"[ACTIONNAME] [args...]\"",
        isPlayerOnly = true,
        pluginClass = XG7Lobby.class
)
public class ExecuteActionCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() == 0) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        String actionArgs = args.get(0,String.class) + " ";

        if (args.len() > 1) {
            actionArgs = Strings.join(Arrays.asList(args.getArgs()), ' ');
        }

        ActionsProcessor.process(Collections.singletonList(actionArgs), (Player) sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? Arrays.stream(ActionType.values()).map(type -> "[" + type.name() + "] ").collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.matchXMaterial("COMMAND_BLOCK").orElse(XMaterial.CRAFTING_TABLE), this);
    }
}
