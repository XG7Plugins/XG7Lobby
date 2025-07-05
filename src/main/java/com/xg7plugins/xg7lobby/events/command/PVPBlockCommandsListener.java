package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PVPBlockCommandsListener implements Listener {
    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isEnabled();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if (!XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;

        PVPConfigs config = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

        if (config.getCommandsBlocked().contains(event.getMessage().split(" ")[0])) {
            event.setCancelled(true);
            Text.fromLang(event.getPlayer(),XG7Lobby.getInstance(), "pvp.pvp-command").thenAccept(text -> text.send(event.getPlayer()));
        }
    }
}
