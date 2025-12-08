package com.xg7plugins.xg7lobby.events.command.blocker;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketEventType;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@PacketListenerSetup(packet = PacketEventType.PLAY_SERVER_TAB_COMPLETE)
public class CommandAntiTabListener implements PacketListener {

    private final ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("block-commands");

    @Override
    public boolean isEnabled() {
        return config.get("enabled", false) && config.get("anti-tab", false);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("xg7lobby.command_block_bypass")) return;

        List<String> commands = config.getList("blocked-commands", String.class).orElse(Collections.emptyList());

        WrapperPlayServerTabComplete packet = new WrapperPlayServerTabComplete(event);

        List<WrapperPlayServerTabComplete.CommandMatch> suggestions = packet.getCommandMatches();

        List<WrapperPlayServerTabComplete.CommandMatch> filtered = suggestions.stream().filter(suggestion -> {
            String command = suggestion.getText();
            return !commands.contains(command);
        }).collect(Collectors.toList());

        packet.setCommandMatches(filtered);
    }
}
