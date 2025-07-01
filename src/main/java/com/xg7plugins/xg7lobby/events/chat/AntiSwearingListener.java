package com.xg7plugins.xg7lobby.events.chat;

import com.google.common.base.Strings;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;

public class AntiSwearingListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("xg7lobby.chat.swear")) return;

        if (Config.mainConfigOf(XG7Lobby.getInstance()).get("anti-swearing.anti-swear-only-on-lobby", Boolean.class).orElse(false) && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;


        String message = event.getMessage().toLowerCase();

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        List<String> swearingWords = config.getList("anti-swearing.blocked-words", String.class).orElse(Collections.emptyList());

        boolean dontSend = config.get("anti-swearing.dont-send-the-message", Boolean.class).orElse(false);

        for (String swearingWord : swearingWords) {
            if (!message.contains(swearingWord.toLowerCase())) continue;

            if (dontSend) {
                event.setCancelled(true);
                Text.sendTextFromLang(player,XG7Lobby.getInstance(), "chat.swear");
                return;
            }

            message = message.replace(swearingWord, Strings.repeat(config.get("anti-swearing.replacement", String.class).orElse("*"), swearingWord.length()));

        }

        event.setMessage(message);

    }
}
