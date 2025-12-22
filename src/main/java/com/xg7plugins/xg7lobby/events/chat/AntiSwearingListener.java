package com.xg7plugins.xg7lobby.events.chat;

import com.google.common.base.Strings;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;

public class AntiSwearingListener implements Listener {
    @Override
    public boolean isEnabled() {
        return ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance())
                .section("anti-swearing")
                .get("enabled", false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ConfigSection config = ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance()).section("anti-swearing");

        if (player.hasPermission("xg7lobby.chat.swear")) return;

        boolean antiSwearOnlyInLobby = config.get("anti-swear-only-in-lobby", false);
        if (antiSwearOnlyInLobby && !XG7Plugins.getAPI().isInAnEnabledWorld(XG7LobbyLoader.getInstance(), event.getPlayer())) return;

        String message = event.getMessage().toLowerCase();

        List<String> blockedWords = config.getList("blocked-words", String.class).orElse(Collections.emptyList());

        for (String swearingWord : blockedWords) {
            if (!message.contains(swearingWord.toLowerCase())) continue;

            boolean dontSendMessage = config.get("dont-send-the-message", false);
            if (dontSendMessage) {
                event.setCancelled(true);
                Text.sendTextFromLang(player, XG7LobbyLoader.getInstance(), "chat.swear");
                return;
            }

            String replacement = config.get("replacement", "*");
            message = message.replace(swearingWord, Strings.repeat(replacement, swearingWord.length()));
        }

        event.setMessage(message);
    }
}