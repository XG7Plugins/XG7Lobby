package com.xg7plugins.xg7lobby.commands.utils;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandSetup(
        name = "resetstats",
        permission = "xg7lobby.command.resetstats",
        syntax = "/7lresetstats <ALL | DEATHS | KILLS> <player>",
        description = "Reset a player pvp statistics",
        pluginClass = XG7Lobby.class
)
public class ResetStatsCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            Command.super.onCommand(sender, args);
            return;
        }

        String stats = args.get(0, String.class).toUpperCase();
        OfflinePlayer player = args.get(1, OfflinePlayer.class);

        if (!player.isOnline() && !player.hasPlayedBefore()) {
            CommandMessages.NOT_ONLINE.send(sender);
            return;
        }

        XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
            switch (stats) {
                case "DEATHS":
                    lobbyPlayer.setGlobalPVPDeaths(0);
                    XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                    Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.reset-stats.deaths-reset");
                    return;
                case "KILLS":

                    lobbyPlayer.setGlobalPVPKills(0);
                    lobbyPlayer.setGlobalPVPKillStreak(0);
                    XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                    Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.reset-stats.kills-reset");
                    return;

                case "ALL":

                    lobbyPlayer.setGlobalPVPDeaths(0);
                    lobbyPlayer.setGlobalPVPKills(0);
                    lobbyPlayer.setGlobalPVPKillStreak(0);
                    XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                    Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.reset-stats.on-reset");
                    return;

                default:
                    Command.super.onCommand(sender, args);

            }
        });

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) return Arrays.asList("deaths", "kills", "all");

        return new ArrayList<>(XG7PluginsAPI.getAllPlayerNames());
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.LAVA_BUCKET, this);
    }
}
