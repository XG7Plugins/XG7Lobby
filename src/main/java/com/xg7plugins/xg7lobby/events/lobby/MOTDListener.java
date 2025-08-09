package com.xg7plugins.xg7lobby.events.lobby;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.MotdConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.File;
import java.util.stream.Collectors;

public class MOTDListener implements Listener {
    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), MotdConfig.class).isEnabled();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerPing(ServerListPingEvent event) {

        MotdConfig config = Config.of(XG7Lobby.getInstance(), MotdConfig.class);

        String motd = config.getText().stream()
                .map(line -> {
                    Text text = Text.format(line);
                    text.getTextSender().apply(null, text);
                    return text.getText();
                })
                .collect(Collectors.joining("\n"));
        event.setMotd(motd);
        event.setMaxPlayers(config.getMaxPlayers());

        File file =  new File(XG7Lobby.getInstance().getDataFolder(), "icon.png");

        if (!file.exists()) return;

        try {
            event.setServerIcon(Bukkit.loadServerIcon(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}

