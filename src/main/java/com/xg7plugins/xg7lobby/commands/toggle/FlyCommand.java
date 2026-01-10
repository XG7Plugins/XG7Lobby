package com.xg7plugins.xg7lobby.commands.toggle;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;

import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "fly",
        permission = "xg7lobby.command.fly",
        syntax = "/7lfly (player)",
        description = "Toggle fly mode",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.FEATHER
)
public class FlyCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(isAsync = true, isInEnabledWorldOnly = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        OfflinePlayer target = null;
        boolean isOther = false;
        if (args.len() == 0) {
            if (!(sender instanceof Player)) {
                return CommandState.NOT_A_PLAYER;
            }
            target = (Player) sender;
        }

        if (args.len() > 0) {
            if (!sender.hasPermission("xg7lobby.command.fly-other")) {
                return CommandState.NO_PERMISSION;
            }
            target = args.get(0, OfflinePlayer.class);
            isOther = true;
        }
        if (isOther && target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            return CommandState.PLAYER_NOT_FOUND;
        }
        if (!XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), target.getPlayer())) {
            return CommandState.DISABLED_WORLD;
        }

        boolean finalIsOther = isOther;

        OfflinePlayer finalTarget = target;

        LobbyPlayer lobbyPlayer = XG7Lobby.getAPI().getLobbyPlayer(target.getUniqueId());

        boolean before = lobbyPlayer.isBuildEnabled();

        lobbyPlayer.setFlying(!lobbyPlayer.isFlying());

        try {
            XG7Lobby.getAPI().lobbyPlayerManager().updatePlayer(lobbyPlayer);

            if (finalTarget.isOnline()) {
                XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(lobbyPlayer::fly));
                Text.sendTextFromLang(lobbyPlayer.getPlayer(), XG7Lobby.getInstance(), "commands.fly." + (lobbyPlayer.isFlying() ? "toggle-on" : "toggle-off"));
            }
            if (finalIsOther) Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.fly." + (lobbyPlayer.isFlying() ? "toggle-other-on" : "toggle-other-off"), Pair.of("target", lobbyPlayer.getPlayer().getDisplayName()));

            PlayerMenuHolder playerMenu = XG7Menus.getPlayerMenuHolder(lobbyPlayer.getPlayerUUID());
            if (playerMenu != null) BasicMenu.refresh(playerMenu);

        } catch (Exception e) {
            lobbyPlayer.setFlying(before);
            XG7Lobby.getAPI().lobbyPlayerManager().updatePlayer(lobbyPlayer);
            throw new RuntimeException(e);
        }

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (!sender.hasPermission("xg7lobby.command.fly-other")) return Collections.emptyList();
        return Bukkit.getOnlinePlayers().stream().filter(player -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player)).map(Player::getName).collect(Collectors.toList());
    }
}