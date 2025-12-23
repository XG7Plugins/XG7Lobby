package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;

public class PVPBlockCommandsListener implements Listener {
    @Override
    public boolean isEnabled() {
        return ConfigFile.of("pvp", XG7Lobby.getInstance()).root().get("enabled");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if (!XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;

        ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();

        if (config.getList("blocked-commands", String.class).orElse(Collections.emptyList()).contains(event.getMessage().split(" ")[0])) {
            event.setCancelled(true);
            Text.sendTextFromLang(event.getPlayer(), XG7Lobby.getInstance(), "pvp.pvp-command");
        }
    }
}
