package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import com.xg7plugins.xg7lobby.configs.LaunchpadConfigs;
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
        return Config.of(XG7Lobby.getInstance(), ChatConfigs.AntiSpam.class).isEnabled();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ChatConfigs.AntiSpam config = Config.of(XG7Plugins.getInstance(), ChatConfigs.AntiSpam.class);

        if (player.hasPermission("xg7lobby.chat.spam")) return;

        if (config.isAntiSpamOnlyOnLobby() && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

        String message = event.getMessage().toLowerCase();

        if (XG7PluginsAPI.cooldowns().containsPlayer("lobby-chat-spam", player)) {
            long cooldown = XG7PluginsAPI.cooldowns().getReamingTime("lobby-chat-spam", player);
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "chat.message-cooldown", Pair.of("time", cooldown + ""));
            event.setCancelled(true);
            return;
        }
        if (config.isMessageCannotBeTheSame()) {
            String lastMessage = this.lastMessages.get(player.getUniqueId());
            if (lastMessage != null && lastMessage.equalsIgnoreCase(message)) {
                Text.sendTextFromLang(player,XG7Lobby.getInstance(), "chat.same-message");
                event.setCancelled(true);
                return;
            }
            lastMessages.put(player.getUniqueId(), event.getMessage());
        }

        XG7PluginsAPI.cooldowns().addCooldown(player, "lobby-chat-spam", config.getCooldown().getMilliseconds());

        if (config.getSpamTolerance() <= 0) return;

        AntiSpamTask antiSpamTask = (AntiSpamTask) XG7PluginsAPI.taskManager().getTimerTask(XG7Lobby.getInstance().getName() + ":anti-spam-tolerance");

        antiSpamTask.incrementTolerance(player.getUniqueId());

        if (antiSpamTask.getTolerance(player.getUniqueId()) >= config.getSpamTolerance() ) {
            event.setCancelled(true);
            LobbyPlayer lobbyPlayer = XG7LobbyAPI.getLobbyPlayer(player.getUniqueId());

            LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

            if (lobbyPlayer.isMuted()) return;

            lobbyPlayerManager.addInfraction(new Infraction(lobbyPlayer.getPlayerUUID(), config.getSpamWarnLevel(), "Spamming"));

            if (config.isMuteOnSpamLimit()) {
                lobbyPlayer.setMuted(true);
                if (!config.getUnmuteDelay().isZero()) {
                    lobbyPlayer.setUnmuteTime(Time.now().add(config.getUnmuteDelay()));
                }
            }
            return;
        }

        if (antiSpamTask.getTolerance(player.getUniqueId()) >= config.getSendWarningOnMessage()) {
            Text.sendTextFromLang(player,XG7Lobby.getInstance(), "chat.send-much-messages");
        }


    }

}
