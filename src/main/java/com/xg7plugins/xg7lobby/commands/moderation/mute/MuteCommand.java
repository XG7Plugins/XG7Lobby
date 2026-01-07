package com.xg7plugins.xg7lobby.commands.moderation.mute;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;

import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "mute",
        description = "Mutes a player for chat",
        syntax = "/7lmute <player> <time|forever> (reason)",
        permission = "xg7lobby.moderation.mute",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.IRON_DOOR
)
public class MuteCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(isAsync = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        Time time = args.get(1, String.class).equalsIgnoreCase("forever") ? Time.of(0) : Time.now().add(args.get(1, Time.class));

        String reason = "Muted by an admin";

        if (args.len() > 2) reason = args.join(2);

        ConfigSection moderationConfig = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("moderation");

        LobbyPlayer player = XG7Lobby.getAPI().getLobbyPlayer(target.getUniqueId());

        if (player.isMuted()) {
            return CommandState.error(XG7Lobby.getInstance(), "already-muted");
        }

        if (player.getPlayer().hasPermission("xg7lobby.moderation.mute") && !moderationConfig.get("mute-admin", false)) {
            return CommandState.error(XG7Lobby.getInstance(), "mute-admin");
        }

        LobbyPlayerManager playerManager = XG7Lobby.getAPI().lobbyPlayerManager();

        playerManager.mutePlayer(player, time, Text.format(reason).replaceAll(Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).toMilliseconds()) + "")));

        XG7Lobby.getAPI().lobbyPlayerManager().updatePlayer(player);

        Infraction infraction = new Infraction(player.getPlayerUUID(), moderationConfig.get("mute-warning-level", 1), reason);

        XG7Lobby.getAPI().lobbyPlayerManager().addInfraction(infraction);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.mute.on-mute-sender", Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).toMilliseconds()) + ""), Pair.of("target", target.getName()));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        switch (args.len()) {
            case 1:
                return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
            case 2:
                return Arrays.asList("time", "forever");
            case 3:
                return Collections.singletonList("reason");
            default:
                return Collections.emptyList();
        }
    }
}