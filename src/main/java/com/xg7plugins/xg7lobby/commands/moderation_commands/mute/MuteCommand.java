package com.xg7plugins.xg7lobby.commands.moderation_commands.mute;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.Config;
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
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        OfflinePlayer target = args.get(0,  OfflinePlayer.class);

        Time time = args.get(1, String.class).equalsIgnoreCase("forever") ? Time.of(0) : Time.now().add(args.get(1, Time.class));

        String reason = "Muted by an admin";

        if (args.len() > 2) reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 2, args.len())), ' ');


        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(target.getUniqueId());

        if (player.isMuted()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.mute.already-muted");
            return;
        }

        if (player.getPlayer().hasPermission("xg7lobby.moderation.mute") && !config.get("mute-admin",Boolean.class).orElse(false)) {
            Text.fromLang(sender, XG7Lobby.getInstance(), "commands.mute.mute-admin").thenAccept(text -> text.send(sender));
            return;
        }

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        playerManager.mutePlayer(player, time, Text.format(reason).replaceAll(Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).getMilliseconds()) + "")));

        XG7LobbyAPI.lobbyPlayerManager().updatePlayer(player);

        int warningLevel = config.get("mute-warning-level", Integer.class).orElse(2);

        Infraction infraction = new Infraction(player.getPlayerUUID(), warningLevel, reason);

        XG7LobbyAPI.lobbyPlayerManager().addInfraction(infraction);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.mute.on-mute-sender", Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).getMilliseconds()) + ""), Pair.of("target", target.getName()));

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        switch (args.len()) {
            case 1:
                return new ArrayList<>(XG7PluginsAPI.getAllPlayerNames());
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
