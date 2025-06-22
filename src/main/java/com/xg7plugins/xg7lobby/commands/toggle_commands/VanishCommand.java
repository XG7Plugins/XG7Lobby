package com.xg7plugins.xg7lobby.commands.toggle_commands;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandSetup(
        name = "vanish",
        permission = "xg7lobby.command.vanish",
        description = "Vanish command",
        syntax = "/vanish",
        isPlayerOnly = true,
        isInEnabledWorldOnly = true,
        isAsync = true,
        pluginClass = XG7Lobby.class
)
public class VanishCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        LobbyPlayer lobbyPlayer = XG7LobbyAPI.getLobbyPlayer(((Player) sender).getUniqueId());

        boolean before = lobbyPlayer.isHidingPlayers();

        lobbyPlayer.setHidingPlayers(!lobbyPlayer.isHidingPlayers());

        try {
            XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
        } catch (Exception ex) {
            lobbyPlayer.setFlying(before);
            XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
            throw new RuntimeException(ex);
        }

        PlayerMenuHolder playerMenu = XG7Menus.getPlayerMenuHolder(lobbyPlayer.getPlayerUUID());
        if (playerMenu != null) BasicMenu.refresh(playerMenu);

        XG7PluginsAPI.taskManager().runSyncTask(XG7Lobby.getInstance(), () -> {
            lobbyPlayer.applyHide();
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), lobbyPlayer.isHidingPlayers() ? "hide-players.hide" : "hide-players.show");
        });

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return Command.super.onTabComplete(sender, args);
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
