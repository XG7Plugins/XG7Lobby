package com.xg7plugins.xg7lobby.events.lobby;

import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;
import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.xg7lobby.XG7Lobby;

public class NLoginListener implements Listener {

    @Override
    public boolean isEnabled() {
        return XG7PluginsAPI.isDependencyEnabled("nLogin")
         && ConfigFile.of("events", XG7Lobby.getInstance()).section("on-join").get("apply-configs-after-authenticate", false);
    }

    @EventHandler
    public void onAuthenticationSuccess(AuthenticateEvent event) {
        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
            LoginAndLogoutEvent.handleWorldJoin(event.getPlayer(), event.getPlayer().getWorld(), true);
        }));
    }

}
