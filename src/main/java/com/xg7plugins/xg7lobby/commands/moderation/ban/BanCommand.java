package com.xg7plugins.xg7lobby.commands.moderation.ban;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

@CommandSetup(
        name = "ban",
        description = "Bans a player",
        syntax = "/7lban <player> <time> (reason)",
        permission = "xg7lobby.moderation.ban",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.BARRIER
)
public class BanCommand implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        Time time = args.get(1, String.class).equalsIgnoreCase("forever") ? Time.of(0) : Time.now().add(args.get(1, Time.class));

        String reason;

        if (args.len() > 2) reason = args.toString(2);
        else reason = "Banned by an admin";

        if (target.isBanned()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.ban.already-banned");
            return CommandState.ERROR;
        }

        ConfigSection moderationConfig = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("moderation");

        if (target.isOp() && !moderationConfig.get("ban-admin", false)) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.ban.ban-admin");
            return CommandState.ERROR;
        }

        LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

        lobbyPlayerManager.banPlayer(target, time, Text.fromLang(target.getPlayer(), XG7Lobby.getInstance(), "commands.ban.on-ban").replace("reason", reason).replace("time", (time.isZero() ? "forever" : String.valueOf(Time.getRemainingTime(time).toMilliseconds()))));

        XG7LobbyAPI.requestLobbyPlayer(target.getUniqueId()).thenAccept(lobbyPlayer -> {
            Infraction infraction = new Infraction(target.getUniqueId(), moderationConfig.get("ban-warning-level", 3), reason);

            lobbyPlayerManager.addInfraction(infraction);
        });

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.ban.on-ban-sender", Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).toMilliseconds()) + ""), Pair.of("target", target.getName()));

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