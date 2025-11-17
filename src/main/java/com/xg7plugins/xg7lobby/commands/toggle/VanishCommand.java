package com.xg7plugins.xg7lobby.commands.toggle;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
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
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
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

        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(() -> {
            lobbyPlayer.applyHide();
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), lobbyPlayer.isHidingPlayers() ? "hide-players.hide" : "hide-players.show");
        }));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return Command.super.onTabComplete(sender, args);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.ENDER_EYE, this);
    }
}