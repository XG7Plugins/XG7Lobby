package com.xg7plugins.xg7lobby.events.command.blocker;

import com.xg7plugins.libs.packetevents.event.PacketSendEvent;
import com.xg7plugins.libs.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.events.packetevents.PacketEventType;
import com.xg7plugins.events.packetevents.PacketListenerSetup;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@PacketListenerSetup(packet = PacketEventType.PLAY_SERVER_TAB_COMPLETE)
public class CommandAntiTabListener implements PacketListener {

    private final ChatConfigs config = Config.of(XG7Lobby.getInstance(), ChatConfigs.class);

    @Override
    public boolean isEnabled() {
        return config.isAntiTabEnabled() && config.isBlockCommandsEnabled();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("xg7lobby.command_block_bypass")) return;

        List<String> commands = config.getBlockedCommands();

        WrapperPlayServerTabComplete packet = new WrapperPlayServerTabComplete(event);

        List<WrapperPlayServerTabComplete.CommandMatch> suggestions = packet.getCommandMatches();

        List<WrapperPlayServerTabComplete.CommandMatch> filtered = suggestions.stream().filter(suggestion -> {
            String command = suggestion.getText();

            return !commands.contains(command);
        }).collect(Collectors.toList());

        packet.setCommandMatches(filtered);
    }
}
