package com.xg7plugins.xg7lobby.commands.lobby;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
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
    public void onCommand(CommandSender sender, CommandArgs args) {
        XG7Menus.getInstance().getMenu(XG7Lobby.getInstance(), "lobbies-menu").open((Player) sender);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_EYE, this);
    }
}
