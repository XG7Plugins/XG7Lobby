package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.pvp.event.PlayerLeavePVPEvent;
import com.xg7plugins.xg7lobby.environment.LobbyApplier;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;

public class LeavePVPHandler implements PVPHandler, LobbyListener {

    private final ConfigSection pvpConfigs = ConfigFile.of("pvp", XG7LobbyLoader.getInstance()).root();
    private final ConfigSection config = ConfigFile.of("pvp", XG7LobbyLoader.getInstance()).section("on-leave-pvp");

    @Override
    public void handle(Player player, Object... args) {

        Text.sendTextFromLang(player, XG7LobbyLoader.getInstance(), "pvp.on-leave");

        if (player.isOnline()) {

            if (XG7LobbyAPI.customInventoryManager() != null) {
                XG7Plugins.getAPI().menus().closeAllMenus(player);

                XG7LobbyAPI.customInventoryManager().openMenu(ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance()).root().get("main-selector-id", "selector"), player);
            }

            LobbyApplier.apply(player);

            ActionsProcessor.process(config.getList("actions", String.class).orElse(Collections.emptyList()), player);

            if (pvpConfigs.get("hide-players-not-in-pvp", false)) {
                Bukkit.getOnlinePlayers().stream().filter(XG7LobbyAPI.globalPVPManager()::isInPVP).forEach(p -> p.hidePlayer(player));
            }

            XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                lobbyPlayer.fly();
                XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of( lobbyPlayer::applyBuild));
                lobbyPlayer.applyHide();
            });

        }

        Bukkit.getPluginManager().callEvent(new PlayerLeavePVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.get("enabled", false);

    }

    @Override
    public void onWorldLeave(Player player, World newWorld) {
        XG7LobbyAPI.globalPVPManager().removePlayer(player);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        XG7LobbyAPI.globalPVPManager().removePlayer(event.getPlayer());
    }
}
