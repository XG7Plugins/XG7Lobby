package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteCommandListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(event.getPlayer().getUniqueId());

        if (!player.isMuted()) return;

        if (Time.now().isEqualOrAfter(player.getUnmuteTime()) && !player.getUnmuteTime().isZero()) {
            player.setMuted(false);
            player.setUnmuteTime(Time.of(0));
            XG7LobbyAPI.lobbyPlayerManager().updatePlayer(player);
            return;
        }

        event.setCancelled(true);
        Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "chat.muted",  Pair.of("time", (player.getUnmuteTime().isZero() ? "ever" : Time.getRemainingTime(player.getUnmuteTime()).toMilliseconds()) + ""));
    }
}
