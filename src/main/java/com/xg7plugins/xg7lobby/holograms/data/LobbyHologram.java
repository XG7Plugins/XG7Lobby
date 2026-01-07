package com.xg7plugins.xg7lobby.holograms.data;

import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
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

    private List<String> defaultClickActions = new ArrayList<>();
    private List<String> rightClickActions = new ArrayList<>();
    private List<String> leftClickActions = new ArrayList<>();
    private List<String> shiftRightClickActions = new ArrayList<>();
    private List<String> shiftLeftClickActions = new ArrayList<>();

    public Hologram toHologram() {
        return new Hologram(
                XG7Lobby.getInstance(),
                "xg7lobby_hologram-" + id,
                lines.stream().map(LobbyHologramLine::toHologramLine).collect(Collectors.toList()),
                location,
                (hologramClickEvent) -> {

                    Player player = hologramClickEvent.getPlayer();

                    ActionsProcessor.process(defaultClickActions, player);

                    switch (hologramClickEvent.getAction()) {
                        case RIGHT_CLICK:
                            ActionsProcessor.process(rightClickActions, player);
                            break;
                        case LEFT_CLICK:
                            ActionsProcessor.process(leftClickActions, player);
                            break;
                        case SHIFT_RIGHT:
                            ActionsProcessor.process(shiftRightClickActions, player);
                            break;
                        case SHIFT_LEFT:
                            ActionsProcessor.process(shiftLeftClickActions, player);
                            break;
                    }
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
