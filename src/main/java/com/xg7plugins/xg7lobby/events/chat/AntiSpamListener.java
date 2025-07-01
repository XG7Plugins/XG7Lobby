package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.tasks.AntiSpamTask;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class AntiSpamListener implements Listener {

    private final Config config = Config.mainConfigOf(XG7Lobby.getInstance());

    private final HashMap<UUID, String> lastMessages = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return config.get("anti-spam.enabled", Boolean.class).orElse(false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("xg7lobby.chat.spam")) return;

        if (config.get("anti-spam.anti-spam-only-on-lobby", Boolean.class).orElse(false) && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

        String message = event.getMessage().toLowerCase();

        if (XG7PluginsAPI.cooldowns().containsPlayer("lobby-chat-spam", player)) {
            long cooldown = XG7PluginsAPI.cooldowns().getReamingTime("lobby-chat-spam", player);
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "chat.message-cooldown", Pair.of("time", cooldown + ""));
            event.setCancelled(true);
            return;
        }
        if (config.get("anti-spam.message-cannot-be-the-same", Boolean.class).orElse(true)) {
            String lastMessage = this.lastMessages.get(player.getUniqueId());
            if (lastMessage != null && lastMessage.equalsIgnoreCase(message)) {
                Text.sendTextFromLang(player,XG7Lobby.getInstance(), "chat.same-message");
                event.setCancelled(true);
                return;
            }
            lastMessages.put(player.getUniqueId(), event.getMessage());
        }

        XG7PluginsAPI.cooldowns().addCooldown(player, "lobby-chat-spam", config.getTime("anti-spam.cooldown").orElse(Time.of(15000L)).getMilliseconds());

        if (config.get("anti-spam.spam-tolerance", Integer.class).orElse(0) <= 0) return;

        AntiSpamTask antiSpamTask = (AntiSpamTask) XG7PluginsAPI.taskManager().getTimerTask(XG7Lobby.getInstance().getName() + ":anti-spam-tolerance");

        antiSpamTask.incrementTolerance(player.getUniqueId());

        if (antiSpamTask.getTolerance(player.getUniqueId()) >= config.get("anti-spam.spam-tolerance", Integer.class).orElse(0)) {
            event.setCancelled(true);
            LobbyPlayer lobbyPlayer = XG7LobbyAPI.getLobbyPlayer(player.getUniqueId());

            LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

            if (lobbyPlayer.isMuted()) return;

            lobbyPlayerManager.addInfraction(new Infraction(lobbyPlayer.getPlayerUUID(), config.get("anti-spam.spam-warn-level", Integer.class).orElse(0), "Spamming"));

            if (config.get("anti-spam.mute-on-spam-limit", Boolean.class).orElse(false)) {
                lobbyPlayer.setMuted(true);
                if (!config.getTime("anti-spam.unmute-delay").orElse(Time.of(0)).isZero()) {
                    lobbyPlayer.setUnmuteTime(Time.now().add(config.getTime("anti-spam.unmute-delay").orElse(Time.of(50000L))));
                }
            }
            return;
        }

        if (antiSpamTask.getTolerance(player.getUniqueId()) >= config.get("anti-spam.send-warning-on-message", Integer.class).orElse(0)) {
            Text.sendTextFromLang(player,XG7Lobby.getInstance(), "chat.send-much-messages");
        }


    }

}
