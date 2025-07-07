package com.xg7plugins.xg7lobby.commands.lobby;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.LobbyTeleportConfigs;
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
        pluginClass = XG7Lobby.class
)
public class Lobby implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        String id = null;

        if (args.len() > 0) {
            if (!sender.hasPermission("xg7lobby.command.lobby.teleport-id")) {
                CommandMessages.NO_PERMISSION.send(sender);
                return;
            }
            id = args.get(0, String.class);
        }

        Player targetToTeleport;
        boolean targetIsOther = false;

        if (args.len() > 1) {
            if (!sender.hasPermission("xg7lobby.command.lobby.teleport-others")) {
                CommandMessages.NO_PERMISSION.send(sender);
                return;
            }
            OfflinePlayer targetOffline = args.get(1, OfflinePlayer.class);

            if (!targetOffline.hasPlayedBefore() || !targetOffline.isOnline()) {
                CommandMessages.PLAYER_NOT_FOUND.send(sender);
                return;
            }

            targetToTeleport = targetOffline.getPlayer();
            targetIsOther = !targetToTeleport.getName().equals(sender.getName());
        } else {
            if (!(sender instanceof Player)) {
                CommandMessages.NOT_A_PLAYER.send(sender);
                return;
            }
            targetToTeleport = (Player) sender;
        }


        LobbyTeleportConfigs config = Config.of(XG7Lobby.getInstance(), LobbyTeleportConfigs.class);

        CooldownManager cooldownManager = XG7PluginsAPI.cooldowns();

        if (cooldownManager.containsPlayer("lobby-cooldown-after", targetToTeleport)) {

            long cooldownToToggle = cooldownManager.getReamingTime("lobby-cooldown-after", targetToTeleport);
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.on-teleport.on-cooldown" + (targetIsOther ? "-other" : ""),
                    Pair.of("target", targetToTeleport.getName()),
                    Pair.of("time", String.valueOf(cooldownToToggle))
            );
            return;
        }

        if (cooldownManager.containsPlayer("lobby-cooldown-before", targetToTeleport) && !targetIsOther) {
            cooldownManager.removeCooldown("lobby-cooldown-before", targetToTeleport.getUniqueId());
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.teleport-cancelled");
            return;
        }

        Player finalTargetToTeleport = targetToTeleport;

        boolean finalTargetIsOther = targetIsOther;
        Consumer<LobbyLocation> teleportConsumer = lobby -> {

            if (lobby == null) {
                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.on-teleport.on-error-doesnt-exist" + (sender.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                return;
            }

            if (finalTargetToTeleport.hasPermission("xg7lobby.command.lobby.bypass-cooldown")) {
                XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> lobby.teleport(finalTargetToTeleport)));
                return;
            }

            cooldownManager.addCooldown(finalTargetToTeleport,
                    new CooldownManager.CooldownTask(
                            "lobby-cooldown-before",
                            config.getBeforeTeleport().getMilliseconds(),
                            player -> Text.fromLang(
                                    player,
                                    XG7Lobby.getInstance(),
                                    "lobby.on-teleporting-message"
                            ).thenAccept(text -> {
                                long cooldownToToggle = cooldownManager.getReamingTime("lobby-cooldown-before", finalTargetToTeleport);
                                text.replace("target", finalTargetToTeleport.getName())
                                        .replace("time", String.valueOf(cooldownToToggle))
                                        .send(player);
                            }),
                            ((player, error) -> {
                                if (error) {
                                    Text.sendTextFromLang(player, XG7Lobby.getInstance(), "lobby.teleport-cancelled");
                                    return;
                                }
                                XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> lobby.teleport(finalTargetToTeleport)));
                                cooldownManager.addCooldown(finalTargetToTeleport, "lobby-cooldown-after", config.getAfterTeleport().getMilliseconds());
                            })
                    )
            );
            if (finalTargetIsOther) Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.on-teleport.on-success-other", Pair.of("target", finalTargetToTeleport.getName()));

        };

        if (id == null) {
            XG7LobbyAPI.requestRandomLobbyLocation().thenAccept(teleportConsumer);
            return;
        }

        XG7LobbyAPI.requestLobbyLocation(id).thenAccept(teleportConsumer);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1 && sender.hasPermission("xg7lobby.command.lobby.teleport-id")) {
            return XG7PluginsAPI.database().getCachedEntities().asMap().join().values().stream().filter(ob -> ob instanceof LobbyLocation).map(e -> ((LobbyLocation)e).getID()).collect(Collectors.toList());
        }
        if (args.len() == 2 && sender.hasPermission("xg7lobby.command.lobby.teleport-others")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.BLAZE_ROD, this);
    }
}
