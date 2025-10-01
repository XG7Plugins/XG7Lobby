package com.xg7plugins.xg7lobby.commands.toggle;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.cooldowns.CooldownManager;

import com.xg7plugins.config.utils.ConfigCheck;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandSetup(
        name = "pvp",
        permission = "xg7lobby.pvp",
        syntax = "/7lpvp",
        description = "Enable or disable PVP",
        isInEnabledWorldOnly = true,
        isPlayerOnly = true,
        pluginClass = XG7Lobby.class,
        isEnabled = @ConfigCheck(
                configName = "pvp",
                path = "enabled"
        )
)
public class PVPCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        Player player = (Player) sender;

        if (!XG7LobbyAPI.isPlayerInPVP(player)) {
            XG7LobbyAPI.globalPVPManager().addPlayer(player);
            return CommandState.FINE;
        }

        ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();

        if (XG7PluginsAPI.cooldowns().containsPlayer("pvp-disable", player)) {
            XG7PluginsAPI.cooldowns().removeCooldown("pvp-disable", player.getUniqueId(), true);
            return CommandState.FINE;
        }

        XG7PluginsAPI.cooldowns().addCooldown(player, new CooldownManager.CooldownTask(
                "pvp-disable",
                config.getTimeInMilliseconds("leave-command-cooldown"),
                p -> {

                    long cooldownToToggle = XG7PluginsAPI.cooldowns().getReamingTime("pvp-disable", p);

                    Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.pvp-disabling", Pair.of("player", p.getName()), Pair.of("time", cooldownToToggle + ""));
                },
                (p, b) -> {
                    if (b) {
                        Text.fromLang(player, XG7Lobby.getInstance(), "pvp.disable-cancelled").thenAccept(text -> text.send(player));
                        return;
                    }
                    XG7LobbyAPI.globalPVPManager().getCombatLogHandler().removeFromLog(player);
                    XG7PluginsAPI.taskManager().runSync(BukkitTask.of(() -> XG7LobbyAPI.globalPVPManager().removePlayer(player)));
                })
        );

        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.DIAMOND_SWORD, this);
    }
}