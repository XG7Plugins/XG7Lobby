package com.xg7plugins.xg7lobby.commands.moderation.mute;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "unmute",
        description = "Unmutes a player",
        syntax = "/7plunmute <player>",
        permission = "xg7lobby.moderation.unmute",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.IRON_DOOR
)
public class UnMuteCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(isAsync = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(target.getUniqueId());

        if (player == null) {
            return CommandState.PLAYER_NOT_FOUND;
        }

        if (!player.isMuted()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unmute.not-muted", Pair.of("target", target.getName()));
            return CommandState.ERROR;
        }


        player.setMuted(false);
        player.setUnmuteTime(Time.of(0));
        XG7LobbyAPI.lobbyPlayerManager().updatePlayer(player);

        if (target.isOnline()) {
            Text.sendTextFromLang(target.getPlayer(), XG7Lobby.getInstance(), "commands.unmute.on-unmute");
        }

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unmute.on-unmute-sender", Pair.of("target", player.getOfflinePlayer().getName()));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames()) : Collections.emptyList();
    }

}