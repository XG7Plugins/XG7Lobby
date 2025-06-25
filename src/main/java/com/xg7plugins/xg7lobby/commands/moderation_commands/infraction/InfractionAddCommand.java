package com.xg7plugins.xg7lobby.commands.moderation_commands.infraction;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Map;

@CommandSetup(
        name = "add",
        description = "Adds a infraction to a player",
        syntax = "/7linfraction add <player> <infraction level> <reason>",
        pluginClass = XG7Lobby.class,
        isAsync = true
)
public class InfractionAddCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() != 3) {
            CommandMessages.SYNTAX_ERROR.send(sender);
            return;
        }

        OfflinePlayer offlinePlayer = args.get(0,  OfflinePlayer.class);

        if (!offlinePlayer.isOnline()) {
            CommandMessages.NOT_ONLINE.send(sender);
            return;
        }

        int level = args.get(1, Integer.class);
        String reason = args.get(2, String.class);

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(offlinePlayer.getUniqueId());

        if (player == null) {
            CommandMessages.PLAYER_NOT_FOUND.send(sender);
            return;
        }

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());


        if (player.getPlayer().hasPermission("xg7lobby.moderation.infraction.*") && !config.get("warn-admin", Boolean.class).orElse(false)) {
            Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.warn-admin");
            return;
        }

        if (config.getList("infraction-levels", Map.class).orElse(new ArrayList<>()).stream().noneMatch(map -> (int) map.get("level") == level)) {
            Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.level-invalid");
            return;
        }

        Infraction infraction = new Infraction(player.getPlayerUUID(), level, reason);

        XG7LobbyAPI.lobbyPlayerManager().addInfraction(infraction);

        Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-warn", Pair.of("reason", reason));
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.on-warn-sender", Pair.of("target", player.getPlayer().getName()), Pair.of("reason", reason));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.GREEN_WOOL, this);
    }
}
