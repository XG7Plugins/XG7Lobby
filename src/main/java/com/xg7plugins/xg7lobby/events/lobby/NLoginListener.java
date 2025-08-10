package com.xg7plugins.xg7lobby.events.lobby;

import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.EventConfigs;

public class NLoginListener implements Listener {

    @Override
    public boolean isEnabled() {
        return XG7PluginsAPI.isDependencyEnabled("nLogin")
         && Config.of(XG7Lobby.getInstance(), EventConfigs.OnJoin.class).isApplyConfigsAfterAuthenticate();
    }

    @EventHandler
    public void onAuthenticationSuccess(AuthenticateEvent event) {
        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
            LoginAndLogoutEvent.handleWorldJoin(event.getPlayer(), event.getPlayer().getWorld(), true);
        }));
    }

}
