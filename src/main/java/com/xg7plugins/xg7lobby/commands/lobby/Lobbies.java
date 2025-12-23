package com.xg7plugins.xg7lobby.commands.lobby;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandSetup(
        name = "lobbies",
        syntax = "/7llobbies",
        description = "See all lobbies",
        permission = "xg7lobby.command.lobby.see-lobbies",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.ENDER_EYE
)
public class Lobbies implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender) {
        XG7Plugins.getAPI().menus().getMenu(XG7Lobby.getInstance(), "lobbies-menu").open((Player) sender);
        return CommandState.FINE;
    }

}