package com.xg7plugins.xg7lobby.commands.moderation;


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
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
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
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer offlineTarget = args.get(0, OfflinePlayer.class);

        String reason;

        if (args.len() > 1) reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 1, args.len())), ' ');
        else reason = "Kicked by an admin";


        if (!offlineTarget.isOnline()) {
            return CommandState.NOT_ONLINE;
        }

        Player target = offlineTarget.getPlayer();

        ConfigSection moderationConfig = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("moderation");

        if (target.hasPermission("xg7lobby.moderation.kick") && !moderationConfig.get("kick-admin", false)) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.kick.kick-admin");
            return CommandState.ERROR;
        }

        LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

        lobbyPlayerManager.kickPlayer(target, Text.fromLang(target.getPlayer(), XG7Lobby.getInstance(), "commands.kick.on-kick").join().replace("reason", reason));

        XG7LobbyAPI.requestLobbyPlayer(target.getUniqueId()).thenAccept(lobbyPlayer -> {
            int warningLevel = moderationConfig.get("kick-warning-level", 2);

            Infraction infraction = new Infraction(target.getUniqueId(), warningLevel, reason);
            lobbyPlayerManager.addInfraction(infraction);

        });

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.kick.on-kick-sender", Pair.of("reason", reason), Pair.of("target", target.getName()));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        switch (args.len()) {
            case 1:
                return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
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