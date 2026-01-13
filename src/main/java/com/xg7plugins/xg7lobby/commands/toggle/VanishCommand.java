package com.xg7plugins.xg7lobby.commands.toggle;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;

import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandSetup(
        name = "vanish",
        permission = "xg7lobby.command.vanish",
        description = "Vanish command",
        syntax = "/vanish",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.ENDER_EYE
)
public class VanishCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(
            isPlayerOnly = true,
            isInEnabledWorldOnly = true,
            isAsync = true
    )
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        LobbyPlayer lobbyPlayer = XG7Lobby.getAPI().getLobbyPlayer(((Player) sender).getUniqueId());

        boolean before = lobbyPlayer.getLobbySettings().isHidingPlayers();

        lobbyPlayer.getLobbySettings().setHidingPlayers(!lobbyPlayer.getLobbySettings().isHidingPlayers());

        try {
            XG7Lobby.getAPI().lobbyPlayerManager().updatePlayer(lobbyPlayer);
        } catch (Exception ex) {
            lobbyPlayer.getLobbySettings().setHidingPlayers(before);
            XG7Lobby.getAPI().lobbyPlayerManager().updatePlayer(lobbyPlayer);
            throw new RuntimeException(ex);
        }

        PlayerMenuHolder playerMenu = XG7Menus.getPlayerMenuHolder(lobbyPlayer.getPlayerUUID());
        if (playerMenu != null) BasicMenu.refresh(playerMenu);

        lobbyPlayer.applyHide();
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), lobbyPlayer.getLobbySettings().isHidingPlayers() ? "hide-players.hide" : "hide-players.show");

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return Command.super.onTabComplete(sender, args);
    }

}