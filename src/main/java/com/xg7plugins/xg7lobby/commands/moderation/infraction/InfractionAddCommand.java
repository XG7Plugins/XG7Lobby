package com.xg7plugins.xg7lobby.commands.moderation.infraction;

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
import com.xg7plugins.xg7lobby.configs.ModerationConfigs;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@CommandSetup(
        name = "add",
        description = "Adds an infraction to a player",
        syntax = "/7linfraction add <player> <infraction level> <reason>",
        pluginClass = XG7Lobby.class,
        isAsync = true
)
public class InfractionAddCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 3) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandSetup().syntax());
            return;
        }

        OfflinePlayer offlinePlayer = args.get(0,  OfflinePlayer.class);

        int level = args.get(1, Integer.class);
        String reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 2, args.len())), ' ');

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(offlinePlayer.getUniqueId());

        if (player == null) {
            CommandMessages.PLAYER_NOT_FOUND.send(sender);
            return;
        }

        ModerationConfigs config = Config.of(XG7Lobby.getInstance(), ModerationConfigs.class);


        if (player.getOfflinePlayer().isOp() && !config.isWarnAdmin()) {
            Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.warn-admin");
            return;
        }

        if (config.getInfractionLevels().stream().noneMatch(map -> (int) map.get("level") == level)) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.level-invalid");
            return;
        }

        Infraction infraction = new Infraction(player.getPlayerUUID(), level, reason);

        XG7LobbyAPI.lobbyPlayerManager().addInfraction(infraction);

        if (offlinePlayer.isOnline()) Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-warn", Pair.of("reason", reason));
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.on-warn-sender", Pair.of("target", player.getOfflinePlayer().getName()), Pair.of("reason", reason));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.GREEN_WOOL, this);
    }
}
