package com.xg7plugins.xg7lobby.commands.moderation.infraction;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.menus.default_menus.infractions_menu.InfractionsMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "infractions",
        description = "Open the infraction menu of a player",
        syntax = "/7linfractions (player)",
        permission = "xg7lobby.command.infractions",
        isPlayerOnly = true,
        pluginClass = XG7Lobby.class
)
public class InfractionsMenuCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        Player player = (Player) sender;
        OfflinePlayer target = args.len() == 0 ? player : args.get(0, OfflinePlayer.class);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            return CommandState.PLAYER_NOT_FOUND;
        }

        InfractionsMenu infractionsMenu = XG7Plugins.getAPI().menus().getMenu(XG7Lobby.getInstance(), "warns-menu");

        infractionsMenu.open(player, target);

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return sender.hasPermission("xg7lobby.command.infractions-other") ? new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames()) : Collections.emptyList();
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.PAPER, this);
    }
}