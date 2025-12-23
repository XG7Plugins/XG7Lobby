package com.xg7plugins.xg7lobby.commands.toggle;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.cooldowns.CooldownManager;

import com.xg7plugins.config.utils.ConfigCheck;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandSetup(
        name = "pvp",
        permission = "xg7lobby.pvp",
        syntax = "/7lpvp",
        description = "Enable or disable PVP",
        pluginClass = XG7Lobby.class,
        isEnabled = @ConfigCheck(
                configName = "pvp",
                path = "enabled"
        ),
        iconMaterial = XMaterial.DIAMOND_SWORD
)
public class PVPCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(isInEnabledWorldOnly = true, isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        Player player = (Player) sender;

        if (!XG7LobbyAPI.isPlayerInPVP(player)) {
            XG7LobbyAPI.globalPVPManager().addPlayer(player);
            return CommandState.FINE;
        }

        ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();

        if (XG7Plugins.getAPI().cooldowns().containsPlayer("pvp-disable", player)) {
            XG7Plugins.getAPI().cooldowns().removeCooldown("pvp-disable", player.getUniqueId(), true);
            return CommandState.FINE;
        }

        XG7Plugins.getAPI().cooldowns().addCooldown(player, new CooldownManager.CooldownTask(
                "pvp-disable",
                config.getTimeInMilliseconds("leave-command-cooldown"),
                p -> {

                    long cooldownToToggle = XG7Plugins.getAPI().cooldowns().getReamingTime("pvp-disable", p);

                    Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.pvp-disabling", Pair.of("player", p.getName()), Pair.of("time", cooldownToToggle + ""));
                },
                (p, b) -> {
                    if (b) {
                        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.disable-cancelled");
                        return;
                    }
                    XG7LobbyAPI.globalPVPManager().getCombatLogHandler().removeFromLog(player);
                    XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of(() -> XG7LobbyAPI.globalPVPManager().removePlayer(player)));
                })
        );

        return CommandState.FINE;
    }
}