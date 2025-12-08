package com.xg7plugins.xg7lobby.commands.moderation.mute;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "mute",
        description = "Mutes a player for chat",
        syntax = "/7lmute <player> <time> (reason)",
        permission = "xg7lobby.moderation.mute",
        pluginClass = XG7Lobby.class,
        isAsync = true
)
public class MuteCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        Time time = args.get(1, String.class).equalsIgnoreCase("forever") ? Time.of(0) : Time.now().add(args.get(1, Time.class));

        String reason = "Muted by an admin";

        if (args.len() > 2) reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 2, args.len())), ' ');

        ConfigSection moderationConfig = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("moderation");

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(target.getUniqueId());

        if (player.isMuted()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.mute.already-muted");
            return CommandState.ERROR;
        }

        if (player.getPlayer().hasPermission("xg7lobby.moderation.mute") && !moderationConfig.get("mute-admin", false)) {
            Text.fromLang(sender, XG7Lobby.getInstance(), "commands.mute.mute-admin").thenAccept(text -> text.send(sender));
            return CommandState.ERROR;
        }

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        playerManager.mutePlayer(player, time, Text.format(reason).replaceAll(Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).toMilliseconds()) + "")));

        XG7LobbyAPI.lobbyPlayerManager().updatePlayer(player);

        Infraction infraction = new Infraction(player.getPlayerUUID(), moderationConfig.get("mute-warning-level", 1), reason);

        XG7LobbyAPI.lobbyPlayerManager().addInfraction(infraction);

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

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.IRON_DOOR, this);
    }
}