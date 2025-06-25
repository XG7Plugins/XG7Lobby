package com.xg7plugins.xg7lobby.events.lobby_events;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MOTDListener implements Listener {
    @Override
    public boolean isEnabled() {
        return Config.mainConfigOf(XG7Lobby.getInstance()).get("motd.enabled", Boolean.class).orElse(false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerPing(ServerListPingEvent event) {

        Config config =  Config.mainConfigOf(XG7Lobby.getInstance());

        int maxPlayers = config.get("motd.max-players", Integer.class).orElse(0);

        List<String> motdLines = config.getList("motd.text", String.class).orElse(Collections.emptyList());
        String motd = motdLines.stream()
                .map(line -> {
                    Text text = Text.format(line);
                    text.getTextSender().apply(null, text);
                    return text.getText();
                })
                .collect(Collectors.joining("\n"));
        event.setMotd(motd);
        event.setMaxPlayers(maxPlayers);

        File file =  new File(XG7Lobby.getInstance().getDataFolder(), "icon.png");

        if (!file.exists()) return;

        try {
            event.setServerIcon(Bukkit.loadServerIcon(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}

