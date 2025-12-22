package com.xg7plugins.xg7lobby.events.lobby;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.File;
import java.util.Collections;
import java.util.stream.Collectors;

public class MOTDListener implements Listener {
    @Override
    public boolean isEnabled() {
         return ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance()).section("motd").get("enabled", false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerPing(ServerListPingEvent event) {

        ConfigSection config = ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance()).section("motd");

        String motd = config.getList("text", String.class).orElse(Collections.emptyList()).stream()
                .map(line -> {
                    Text text = Text.format(line);
                    text.getTextSender().apply(null, text);
                    return text.getText();
                })
                .collect(Collectors.joining("\n"));
        event.setMotd(motd);
        event.setMaxPlayers(config.get("max-players", 20));

        File file =  new File(XG7LobbyLoader.getInstance().getDataFolder(), "icon.png");

        if (!file.exists()) return;

        try {
            event.setServerIcon(Bukkit.loadServerIcon(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}

