package com.xg7plugins.xg7lobby.commands.custom;

import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CustomCommandExecutor implements CommandExecutor {

    private final CustomCommandManager customCommandManager;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            CommandState.NOT_A_PLAYER.send(commandSender);
            return true;
        }

        Player player = (Player) commandSender;

        if (!XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player)) {
            CommandState.DISABLED_WORLD.send(player);
            return true;
        }

        CustomCommand customCommand = customCommandManager.getCommand(command.getName());

        if (!player.hasPermission(customCommand.getPermission()) && !customCommand.getPermission().isEmpty()) {
            CommandState.NO_PERMISSION.send(player);
            return true;
        }

        if (!XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player) && customCommand.isEnabledWorldOnly()) {
            CommandState.DISABLED_WORLD.send(player);
            return true;
        }

        customCommand.execute(player);

        return true;
    }
}
