package com.xg7plugins.xg7lobby.events.lobby;

import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;

public class NLoginListener implements Listener {

    @Override
    public boolean isEnabled() {
        return XG7Plugins.getAPI().isDependencyEnabled("nLogin")
         && ConfigFile.of("events", XG7LobbyLoader.getInstance()).section("on-join").get("apply-configs-after-authenticate", false);
    }

    @EventHandler
    public void onAuthenticationSuccess(AuthenticateEvent event) {
        XG7Plugins.getAPI().taskManager().runSync(BukkitTask.of( () -> {
            LoginAndLogoutEvent.handleWorldJoin(event.getPlayer(), event.getPlayer().getWorld(), true);
        }));
    }

}
