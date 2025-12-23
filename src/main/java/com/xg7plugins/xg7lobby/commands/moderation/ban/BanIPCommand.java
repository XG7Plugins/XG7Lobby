package com.xg7plugins.xg7lobby.commands.moderation.ban;

import com.xg7plugins.libs.xseries.XMaterial;
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
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

@CommandSetup(
        name = "banip",
        description = "Bans a player by ip",
        syntax = "/7lbanip <player> <time> (reason)",
        permission = "xg7lobby.moderation.ban",
        pluginClass = XG7Lobby.class
)
public class BanIPCommand implements Command {

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

        String reason;

        if (args.len() > 2) reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 2, args.len())), ' ');
        else reason = "Banned by an admin";

        if (target.isBanned()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.ban.already-banned");
            return CommandState.ERROR;
        }

        ConfigSection moderationConfig = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("moderation");

        if (target.isOp() && !moderationConfig.get("ban-admin", false)) {
            Text.fromLang(sender, XG7Lobby.getInstance(), "commands.ban.ban-admin").thenAccept(text -> text.send(sender));
            return CommandState.ERROR;
        }

        if (!target.isOnline()) {
            return CommandState.NOT_ONLINE;
        }

        LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

        lobbyPlayerManager.banIpPlayer(target.getPlayer(), time, Text.fromLang(target.getPlayer(), XG7Lobby.getInstance(), "commands.ban.on-ban").join().replace("reason", reason).replace("time", String.valueOf(time.toMilliseconds())));

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

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR), this);
    }

}