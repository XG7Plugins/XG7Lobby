package com.xg7plugins.xg7lobby.holograms.data;

import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import lombok.Data;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LobbyHologram {

    private final String id;
    private Location location;

    private List<LobbyHologramLine> lines = new ArrayList<>();
    private List<String> clickActions = new ArrayList<>();

    public Hologram toHologram() {
        return new Hologram(
                XG7Lobby.getInstance(),
                "xg7lobby_hologram-" + id,
                lines.stream().map(LobbyHologramLine::toHologramLine).collect(Collectors.toList()),
                location,
                (hologramClickEvent) -> {

                    Player player = hologramClickEvent.getPlayer();

                    ActionsProcessor.process(clickActions, player, Pair.of("click_action", hologramClickEvent.getAction().name()));

                    //If You need to check the click action:
                    //Usage: [MESSAGE] ?EQUALS: %click_action% = RIGHT_CLICK? Your message here
                }
        );
    }

    public void addLine(LobbyHologramLine line) {
        lines.add(line);
    }

    public  void removeLine(int index) {
        lines.remove(index);
    }



}
