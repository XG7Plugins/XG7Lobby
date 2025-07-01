package com.xg7plugins.xg7lobby.commands.moderation_commands;


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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "kick",
        description = "Kicks a player",
        syntax = "/7lkick <player> (reason)",
        permission = "xg7lobby.moderation.kick",
        pluginClass = XG7Lobby.class
)
public class KickCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        OfflinePlayer offlineTarget = args.get(0, OfflinePlayer.class);

        String reason;

        if (args.len() > 1) reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 1, args.len())), ' ');
        else reason = "Kicked by an admin";


        if (!offlineTarget.isOnline()) {
            CommandMessages.NOT_ONLINE.send(sender);
            return;
        }

        Player target = offlineTarget.getPlayer();

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        if (target.hasPermission("xg7lobby.moderation.mute") && !config.get("kick-admin",Boolean.class).orElse(false)) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.kick.kick-admin");
            return;
        }

        LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

        lobbyPlayerManager.kickPlayer(target, Text.fromLang(target.getPlayer(), XG7Lobby.getInstance(), "commands.kick.on-kick").join().replace("reason", reason));

        XG7LobbyAPI.requestLobbyPlayer(target.getUniqueId()).thenAccept(lobbyPlayer -> {
            int warningLevel = config.get("kick-warning-level", Integer.class).orElse(2);

            Infraction infraction = new Infraction(target.getUniqueId(), warningLevel, reason);
            lobbyPlayerManager.addInfraction(infraction);

        });

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.kick.on-kick-sender", Pair.of("reason", reason), Pair.of("target", target.getName()));

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        switch (args.len()) {
            case 1:
                return new ArrayList<>(XG7PluginsAPI.getAllPlayerNames());
            case 2:
                return Collections.singletonList("reason");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.GOLDEN_BOOTS, this);
    }

}
