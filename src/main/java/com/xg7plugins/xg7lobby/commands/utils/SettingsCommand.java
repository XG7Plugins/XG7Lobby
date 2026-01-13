package com.xg7plugins.xg7lobby.commands.utils;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandSetup(
        name = "settings",
        description = "Parent command for settings commands",
        syntax = "/settings",
        permission = "xg7lobby.command.settings",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.COMPARATOR
)
public class SettingsCommand implements Command {

    @CommandConfig(isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender) {
        XG7Plugins.getAPI().menus().getMenu(XG7Lobby.keyOf("settings-menu")).open((Player) sender);
        return CommandState.FINE;
    }

}
