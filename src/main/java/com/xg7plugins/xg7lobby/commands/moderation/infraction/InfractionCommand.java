package com.xg7plugins.xg7lobby.commands.moderation.infraction;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

@CommandSetup(
        name = "infraction",
        description = "Manages the infractions of a player",
        permission = "xg7lobby.moderation.infraction.*",
        syntax = "/7linfraction <add|pardon>",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.IRON_AXE
)
public class InfractionCommand implements Command {

    @CommandConfig(
            name = "add",
            isAsync = true,
            syntax = "/xg7lobby infraction add <player> <level> <reason>",
            description = "Deletes a task from the tasks registry",
            permission = "xg7lobby.moderation.infraction.add",
            iconMaterial = XMaterial.GREEN_WOOL
    )
    public CommandState add(CommandSender sender, CommandArgs args) {

        if (args.len() < 3) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer offlinePlayer = args.get(0, OfflinePlayer.class);

        int level = args.get(1, Integer.class);
        String reason = args.toString(2);
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

    @CommandConfig(
            name = "pardon",
            isAsync = true,
            syntax = "/xg7lobby infraction pardon <id>",
            description = "Pardons an infraction by its ID",
            permission = "xg7lobby.moderation.infraction.pardon",
            iconMaterial = XMaterial.RED_WOOL
    )
    public CommandState remove(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String id = args.get(0, String.class);

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        Infraction infraction = playerManager.getInfraction(id);

        if (infraction == null) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.infraction-not-found");
            return CommandState.ERROR;
        }

        playerManager.deleteInfraction(infraction);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.on-pardon", Pair.of("id", id));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) {
            return Command.super.onTabComplete(sender, args);
        }

        if (args.get(0, String.class).equalsIgnoreCase("pardon") && args.len() == 2) {
            if (!sender.hasPermission("xg7lobby.command.infraction.pardon")) return Collections.emptyList();
            return XG7LobbyAPI.lobbyPlayerManager().getAllInfractions().stream().map(Infraction::getID).collect(Collectors.toList());
        }

        if (!sender.hasPermission("xg7lobby.command.infraction.add")) return Collections.emptyList();


        switch (args.len()) {
            case 2:
                return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
            case 3:
                return Arrays.asList("1", "2", "3", "level");
            case 4:
                return Collections.singletonList("reason");
            default:
                return Collections.emptyList();
        }
    }
}
