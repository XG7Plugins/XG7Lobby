package com.xg7plugins.xg7lobby.commands.moderation.ban;

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
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        Time time = args.get(1, String.class).equalsIgnoreCase("forever") ? Time.of(0) : Time.now().add(args.get(1, Time.class));

        String reason;

        if (args.len() > 2) reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 2, args.len())), ' ');
        else reason = "Banned by an admin";

        if (target.isBanned()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.ban.already-banned");
            return;
        }

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        if (target.isOp() && !config.get("ban-admin",Boolean.class).orElse(false)) {
            Text.fromLang(sender, XG7Lobby.getInstance(), "commands.ban.ban-admin").thenAccept(text -> text.send(sender));
            return;
        }

        if (!target.isOnline()) {
            CommandMessages.NOT_ONLINE.send(sender);
            return;
        }

        LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

        lobbyPlayerManager.banIpPlayer(target.getPlayer(), time, Text.fromLang(target.getPlayer(), XG7Lobby.getInstance(), "commands.ban.on-ban").join().replace("reason", reason).replace("time", String.valueOf(time.getMilliseconds())));

        XG7LobbyAPI.requestLobbyPlayer(target.getUniqueId()).thenAccept(lobbyPlayer -> {
            int warningLevel = config.get("ban-warning-level", Integer.class).orElse(2);

            Infraction infraction = new Infraction(target.getUniqueId(), warningLevel, reason);

            lobbyPlayerManager.addInfraction(infraction);
        });

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.ban.on-ban-sender", Pair.of("reason", reason), Pair.of("time", (time.isZero() ? "forever" : Time.getRemainingTime(time).getMilliseconds()) + ""), Pair.of("target", target.getName()));
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
        return Item.commandIcon(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR), this);
    }

}
