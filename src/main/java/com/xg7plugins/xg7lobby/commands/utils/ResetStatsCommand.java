package com.xg7plugins.xg7lobby.commands.utils;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
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
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.LAVA_BUCKET
)
public class ResetStatsCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String stats = args.get(0, String.class).toUpperCase();
        OfflinePlayer player = args.get(1, OfflinePlayer.class);

        if (!player.isOnline() && !player.hasPlayedBefore()) {
            return CommandState.NOT_ONLINE;
        }

        XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
            switch (stats) {
                case "DEATHS":
                    lobbyPlayer.setGlobalPVPDeaths(0);
                    XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                    Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.reset-stats.deaths-reset", Pair.of("target", player.getName()));
                    return;
                case "KILLS":

                    lobbyPlayer.setGlobalPVPKills(0);
                    lobbyPlayer.setGlobalPVPKillStreak(0);
                    XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                    Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.reset-stats.kills-reset", Pair.of("target", player.getName()));
                    return;

                case "ALL":

                    lobbyPlayer.setGlobalPVPDeaths(0);
                    lobbyPlayer.setGlobalPVPKills(0);
                    lobbyPlayer.setGlobalPVPKillStreak(0);
                    XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                    Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.reset-stats.on-reset", Pair.of("target", player.getName()));
                    return;

                default:
                    // Invalid syntax
                    break;

            }
        });

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) return Arrays.asList("deaths", "kills", "all");

        return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
    }

}