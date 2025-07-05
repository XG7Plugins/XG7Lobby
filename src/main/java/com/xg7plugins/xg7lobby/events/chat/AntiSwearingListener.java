package com.xg7plugins.xg7lobby.events.chat;

import com.google.common.base.Strings;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;

public class AntiSwearingListener implements Listener {
    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), ChatConfigs.AntiSwearing.class).isEnabled();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ChatConfigs.AntiSwearing config = Config.of(XG7Plugins.getInstance(), ChatConfigs.AntiSwearing.class);

        if (player.hasPermission("xg7lobby.chat.swear")) return;

        if (config.isAntiSwearOnlyOnLobby() && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

        String message = event.getMessage().toLowerCase();

        for (String swearingWord : config.getBlockedWords()) {
            if (!message.contains(swearingWord.toLowerCase())) continue;

            if (config.isDontSendTheMessage()) {
                event.setCancelled(true);
                Text.sendTextFromLang(player,XG7Lobby.getInstance(), "chat.swear");
                return;
            }

            message = message.replace(swearingWord, Strings.repeat(config.getReplacement(), swearingWord.length()));

        }

        event.setMessage(message);

    }
}
