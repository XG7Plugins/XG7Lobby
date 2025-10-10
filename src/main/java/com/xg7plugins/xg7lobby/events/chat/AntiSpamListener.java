package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
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

    private final HashMap<UUID, String> lastMessages = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return ConfigFile.mainConfigOf(XG7Lobby.getInstance())
                .section("anti-spam")
                .get("enabled", false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("anti-spam");

        if (player.hasPermission("xg7lobby.chat.spam")) return;

        boolean antiSpamOnlyInLobby = config.get("anti-spam-only-in-lobby", false);
        if (antiSpamOnlyInLobby && !XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

        String message = event.getMessage().toLowerCase();

        if (XG7Plugins.getAPI().cooldowns().containsPlayer("lobby-chat-spam", player)) {
            long cooldown = XG7Plugins.getAPI().cooldowns().getReamingTime("lobby-chat-spam", player);
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "chat.message-cooldown", Pair.of("time", cooldown + ""));
            event.setCancelled(true);
            return;
        }

        boolean messageCannotBeTheSame = config.get("message-cannot-be-the-same", true);
        if (messageCannotBeTheSame) {
            String lastMessage = this.lastMessages.get(player.getUniqueId());
            if (lastMessage != null && lastMessage.equalsIgnoreCase(message)) {
                Text.sendTextFromLang(player, XG7Lobby.getInstance(), "chat.same-message");
                event.setCancelled(true);
                return;
            }
            lastMessages.put(player.getUniqueId(), event.getMessage());
        }

        Time cooldownTime = config.getTime("cooldown");
        XG7Plugins.getAPI().cooldowns().addCooldown(player, "lobby-chat-spam", cooldownTime.toMilliseconds());

        int spamTolerance = config.get("spam-tolerance", 5);
        if (spamTolerance <= 0) return;

        AntiSpamTask antiSpamTask = (AntiSpamTask) XG7Plugins.getAPI().taskManager().getTimerTask(XG7Lobby.getInstance(), "anti-spam-tolerance");

        antiSpamTask.incrementTolerance(player.getUniqueId());

        if (antiSpamTask.getTolerance(player.getUniqueId()) >= spamTolerance) {
            event.setCancelled(true);
            LobbyPlayer lobbyPlayer = XG7LobbyAPI.getLobbyPlayer(player.getUniqueId());

            LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

            if (lobbyPlayer.isMuted()) return;

            int spamWarnLevel = config.get("spam-warn-level", 1);
            lobbyPlayerManager.addInfraction(new Infraction(lobbyPlayer.getPlayerUUID(), spamWarnLevel, "Spamming"));

            boolean muteOnSpamLimit = config.get("mute-on-spam-limit", true);
            if (muteOnSpamLimit) {
                lobbyPlayer.setMuted(true);
                Time unmuteDelay = config.getTime("unmute-delay");
                if (!unmuteDelay.isZero()) {
                    lobbyPlayer.setUnmuteTime(Time.now().add(unmuteDelay));
                }
            }
            return;
        }

        int sendWarningOnMessage = config.get("send-warning-on-message", 3);
        if (antiSpamTask.getTolerance(player.getUniqueId()) >= sendWarningOnMessage) {
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "chat.send-much-messages");
        }
    }
}