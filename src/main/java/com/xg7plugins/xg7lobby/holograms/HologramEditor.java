package com.xg7plugins.xg7lobby.holograms;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologram;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologramLine;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class HologramEditor {

    @Getter
    private LobbyHologram lobbyHologram;

    public void select(LobbyHologram lobbyHologram) {
        this.lobbyHologram = lobbyHologram;
    }

    public EditError teleport(@NotNull Location location) {
        if (lobbyHologram == null) {
            return EditError.HOLOGRAM_NOT_SELECTED;
        }

        lobbyHologram.setLocation(location);
        return EditError.NONE;
    }

    public Pair<EditError, String> remove() {
        if (lobbyHologram == null) return Pair.of(EditError.HOLOGRAM_NOT_SELECTED, null);

        String id = lobbyHologram.getId();
        this.lobbyHologram = null;

        return Pair.of(EditError.NONE, id);
    }

    public EditError addLine(@NotNull LobbyHologramLine.Type lineType, @NotNull String line) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;

        LobbyHologramLine hologramLine = new LobbyHologramLine(line);
        hologramLine.setType(lineType);
        lobbyHologram.getLines().add(hologramLine);

        return EditError.NONE;
    }

    public EditError editLine(int lineIndex, @NotNull LobbyHologramLine.Type lineType, @NotNull String newContent) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;
        if (lineIndex < 0 || lineIndex >= lobbyHologram.getLines().size()) {
            return EditError.INVALID_LINE_INDEX;
        }

        LobbyHologramLine line = lobbyHologram.getLines().get(lineIndex);
        line.setContent(newContent);
        line.setType(lineType);

        return EditError.NONE;
    }

    public EditError removeLine(int lineIndex) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;
        if (lineIndex < 0 || lineIndex >= lobbyHologram.getLines().size()) {
            return EditError.INVALID_LINE_INDEX;
        }

        lobbyHologram.getLines().remove(lineIndex);
        return EditError.NONE;
    }

    public EditError setOption(int lineIndex, String optionKey, Object optionValue) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;

        LobbyHologramLine line = lobbyHologram.getLines().get(lineIndex);
        line.getOptions().put(optionKey, optionValue);

        return EditError.NONE;
    }

    public List<String> getActions() {
        if (lobbyHologram == null) return Collections.emptyList();

        return lobbyHologram.getClickActions();
    }

    public EditError addAction(@NotNull String action) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;

        lobbyHologram.getClickActions().add(action);
        return EditError.NONE;
    }

    public EditError removeAction(int actionIndex) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;

        if (actionIndex < 0 || actionIndex >= lobbyHologram.getClickActions().size()) {
            return EditError.INVALID_LINE_INDEX;
        }

        lobbyHologram.getClickActions().remove(actionIndex);
        return EditError.NONE;
    }

    public EditError clearActions() {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;

        lobbyHologram.getClickActions().clear();
        return EditError.NONE;
    }

    public EditError editAction(int actionIndex, @NotNull String newAction) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;
        if (actionIndex < 0 || actionIndex >= lobbyHologram.getClickActions().size()) {
            return EditError.INVALID_LINE_INDEX;
        }

        lobbyHologram.getClickActions().set(actionIndex, newAction);
        return EditError.NONE;
    }

    public EditError equip(int lineIndex, EquipmentSlot slot, Item item) {
        if (lobbyHologram == null) return EditError.HOLOGRAM_NOT_SELECTED;
        if (lineIndex < 0 || lineIndex >= lobbyHologram.getLines().size()) {
            return EditError.INVALID_LINE_INDEX;
        }

        LobbyHologramLine line = lobbyHologram.getLines().get(lineIndex);
        line.getEquipment().put(slot, item);

        return EditError.NONE;
    }

    public enum EditError {
        NONE,
        HOLOGRAM_NOT_SELECTED,
        INVALID_LINE_INDEX
    }
}
