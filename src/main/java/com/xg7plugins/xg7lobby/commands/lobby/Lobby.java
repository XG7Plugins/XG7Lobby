package com.xg7plugins.xg7lobby.commands.lobby;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@CommandSetup(
        name = "lobby",
        syntax = "/7llobby (id) (<id> <player>)",
        description = "Teleport to the lobby",
        permission = "xg7lobby.command.lobby.teleport",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.BLAZE_ROD
)
public class Lobby implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        String id = null;

        if (args.len() > 0) {
            if (!sender.hasPermission("xg7lobby.command.lobby.teleport-id")) {
                return CommandState.NO_PERMISSION;
            }
            id = args.get(0, String.class);
        }

        Player targetToTeleport;
        boolean targetIsOther = false;

        if (args.len() > 1) {
            if (!sender.hasPermission("xg7lobby.command.lobby.teleport-others")) {
                return CommandState.NO_PERMISSION;
            }
            OfflinePlayer targetOffline = args.get(1, OfflinePlayer.class);

            if (!targetOffline.hasPlayedBefore() || !targetOffline.isOnline()) {
                return CommandState.PLAYER_NOT_FOUND;
            }

            targetToTeleport = targetOffline.getPlayer();
            targetIsOther = !targetToTeleport.getName().equals(sender.getName());
        } else {
            if (!(sender instanceof Player)) {
                return CommandState.NOT_A_PLAYER;
            }
            targetToTeleport = (Player) sender;
        }

        ConfigSection teleportConfig = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("lobby-teleport-cooldown");

        CooldownManager cooldownManager = XG7Plugins.getAPI().cooldowns();

        if (cooldownManager.containsPlayer("lobby-cooldown-after", targetToTeleport)) {

            long cooldownToToggle = cooldownManager.getReamingTime("lobby-cooldown-after", targetToTeleport);
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.on-teleport.on-cooldown" + (targetIsOther ? "-other" : ""),
                    Pair.of("target", targetToTeleport.getName()),
                    Pair.of("time", String.valueOf(cooldownToToggle))
            );
            return CommandState.ERROR;
        }

        if (cooldownManager.containsPlayer("lobby-cooldown-before", targetToTeleport) && !targetIsOther) {
            cooldownManager.removeCooldown("lobby-cooldown-before", targetToTeleport.getUniqueId(), true);
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.teleport-cancelled");
            return CommandState.ERROR;
        }

        Player finalTargetToTeleport = targetToTeleport;

        boolean finalTargetIsOther = targetIsOther;
        Consumer<LobbyLocation> teleportConsumer = lobby -> {

            if (lobby == null) {
                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.on-teleport.on-error-doesnt-exist" + (sender.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                return;
            }

            if (finalTargetToTeleport.hasPermission("xg7lobby.command.lobby.bypass-cooldown")) {
                XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> lobby.teleport(finalTargetToTeleport)));
                return;
            }

            long cooldownToToggle = cooldownManager.getReamingTime("lobby-cooldown-before", finalTargetToTeleport);

            cooldownManager.addCooldown(finalTargetToTeleport,
                    new CooldownManager.CooldownTask(
                            "lobby-cooldown-before",
                            teleportConfig.getTimeInMilliseconds("before-teleport", 5000L),
                            player -> Text.sendTextFromLang(
                                    player,
                                    XG7Lobby.getInstance(),
                                    "lobby.on-teleporting-message",
                                    Pair.of("target", finalTargetToTeleport.getName()),
                                    Pair.of("time", String.valueOf(cooldownToToggle))
                            ),
                            ((player, error) -> {
                                if (error) {
                                    Text.sendTextFromLang(player, XG7Lobby.getInstance(), "lobby.teleport-cancelled");
                                    return;
                                }
                                XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> lobby.teleport(finalTargetToTeleport)));
                                cooldownManager.addCooldown(finalTargetToTeleport, "lobby-cooldown-after", teleportConfig.getTimeInMilliseconds("after-teleport", 5000L));
                            })
                    )
            );
            if (finalTargetIsOther) Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.on-teleport.on-success-other", Pair.of("target", finalTargetToTeleport.getName()));

        };

        if (id == null) {
            XG7LobbyAPI.requestRandomLobbyLocation().thenAccept(teleportConsumer);
        } else {
            XG7LobbyAPI.requestLobbyLocation(id).thenAccept(teleportConsumer);
        }

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1 && sender.hasPermission("xg7lobby.command.lobby.teleport-id")) {
            return XG7Plugins.getAPI().database().getCachedEntities().asMap().join().values().stream().filter(ob -> ob instanceof LobbyLocation).map(e -> ((LobbyLocation) e).getID()).collect(Collectors.toList());
        }
        if (args.len() == 2 && sender.hasPermission("xg7lobby.command.lobby.teleport-others")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}