package com.xg7plugins.xg7lobby.commands.moderation.infraction;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() < 3) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer offlinePlayer = args.get(0, OfflinePlayer.class);

        int level = args.get(1, Integer.class);
        String reason = Strings.join(Arrays.asList(Arrays.copyOfRange(args.getArgs(), 2, args.len())), ' ');

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(offlinePlayer.getUniqueId());

        if (player == null) {
            return CommandState.PLAYER_NOT_FOUND;
        }

        ConfigSection rootSection = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root();

        if (player.getOfflinePlayer().isOp() && !rootSection.get("warn-admin", false)) {
            Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.warn-admin");
            return CommandState.ERROR;
        }

        List<Map> infractionLevels = rootSection.getList("infraction-levels", Map.class).orElse(Collections.emptyList());
        boolean levelExists = infractionLevels.stream().anyMatch(map -> {
            Object levelObj = map.get("level");
            return levelObj instanceof Integer && (Integer) levelObj == level;
        });

        if (!levelExists) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.level-invalid");
            return CommandState.ERROR;
        }

        Infraction infraction = new Infraction(player.getPlayerUUID(), level, reason);

        XG7LobbyAPI.lobbyPlayerManager().addInfraction(infraction);

        if (offlinePlayer.isOnline()) Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-warn", Pair.of("reason", reason));
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.on-warn-sender", Pair.of("target", player.getOfflinePlayer().getName()), Pair.of("reason", reason));

        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.GREEN_WOOL, this);
    }
}