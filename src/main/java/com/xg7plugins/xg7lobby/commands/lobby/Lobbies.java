package com.xg7plugins.xg7lobby.commands.lobby;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandSetup(
        name = "lobbies",
        syntax = "/7llobbies",
        description = "See all lobbies",
        permission = "xg7lobby.command.lobby.see-lobbies",
        pluginClass = XG7Lobby.class
)
public class Lobbies implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        XG7Plugins.getAPI().menus().getMenu(XG7Lobby.getInstance(), "lobbies-menu").open((Player) sender);
        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_EYE, this);
    }
}