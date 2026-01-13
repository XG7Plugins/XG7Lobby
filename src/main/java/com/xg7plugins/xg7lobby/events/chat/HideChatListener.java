package com.xg7plugins.xg7lobby.events.chat;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.xg7plugins.events.PacketListener;

import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

@PacketListenerSetup
public class HideChatListener implements PacketListener {
    @Override
    public Set<PacketTypeCommon> getHandledEvents() {
        return Collections.singleton(PacketType.Play.Server.CHAT_MESSAGE);
    }

    public void onPacketSend(PacketSendEvent event) {

        Player receiver = event.getPlayer();

        LobbyPlayer lobbyPlayer = XG7Lobby.getAPI().getLobbyPlayer(receiver.getUniqueId());

        if (lobbyPlayer != null && !lobbyPlayer.getLobbySettings().isHidingChat()) return;

        event.setCancelled(true);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
