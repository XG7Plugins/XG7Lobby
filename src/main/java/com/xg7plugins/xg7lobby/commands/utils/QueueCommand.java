package com.xg7plugins.xg7lobby.commands.utils;

import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.config.utils.ConfigCheck;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.queue.QueueManager;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

@CommandSetup(
        name = "queue",
        description = "Manages a player from a queue",
        syntax = "/queue <queueID|add|remove> [action|player] [queueID] [action...]",
        permission = "xg7lobby.command.queue",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.CHORUS_FRUIT,
        isEnabled = @ConfigCheck(
                configName = "config",
                path = "queue-system.enabled"
        )
)
@AllArgsConstructor
public class QueueCommand implements Command {

    private final QueueManager queueManager;

    @CommandConfig(isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        Player player = (Player) sender;

        if (args.len() < 2) return CommandState.SYNTAX_ERROR;

        String id = args.get(0, String.class);
        String action = args.join(1);

        if (!queueManager.exists(id)) {
            return CommandState.error(XG7Lobby.getInstance(), "queue-doesnt-exist", Pair.of("id", id));
        }

        queueManager.addToQueue(id, player, ActionsProcessor.getActionOf(action, Collections.emptyList()));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "add",
            description = "Adds a player to a queue",
            syntax = "/queue add <player> <queueId> <action...>",
            permission = "xg7lobby.command.queue.add",
            iconMaterial = com.cryptomorin.xseries.XMaterial.GREEN_WOOL
    )
    public CommandState onAdd(CommandSender sender, CommandArgs args) {
        if (args.len() < 3) return CommandState.SYNTAX_ERROR;

        String id = args.get(1, String.class);
        String action = args.join(2);

        if (!queueManager.exists(id)) {
            return CommandState.error(XG7Lobby.getInstance(), "queue-doesnt-exist", Pair.of("id", id));
        }

        OfflinePlayer offlinePlayer = args.get(0, OfflinePlayer.class);

        if (!offlinePlayer.isOnline()) return CommandState.NOT_ONLINE;

        queueManager.addToQueue(id, offlinePlayer.getPlayer(), ActionsProcessor.getActionOf(action, Collections.emptyList()));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "remove",
            description = "Removes a player from a queue",
            syntax = "/queue remove <player> (queueId)",
            permission = "xg7lobby.command.queue.remove",
            iconMaterial = com.cryptomorin.xseries.XMaterial.RED_WOOL
    )
    public CommandState onRemove(CommandSender sender, CommandArgs args) {
        if (args.len() < 1 || args.len() > 3) return CommandState.SYNTAX_ERROR;

        OfflinePlayer offlinePlayer = args.get(0, OfflinePlayer.class);
        String id =  args.len() == 2 ? args.get(1, String.class) : null;

        if (!queueManager.exists(id)) {
            return CommandState.error(XG7Lobby.getInstance(), "queue-doesnt-exist", Pair.of("id", id));
        }

        if (!offlinePlayer.isOnline()) return CommandState.NOT_ONLINE;

        if (id == null) queueManager.removeFromAllQueues(offlinePlayer.getPlayer());
        else queueManager.removeFromQueue(id, offlinePlayer.getPlayer());

        return CommandState.FINE;
    }
}
