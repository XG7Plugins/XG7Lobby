package com.xg7plugins.xg7lobby.npcs;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.skin.Skin;
import com.xg7plugins.xg7lobby.holograms.HologramEditor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Getter
public class NPCEditor {

    private LobbyNPC lobbyNPC;

    private final HologramEditor hologramEditor = new HologramEditor();

    public void select(LobbyNPC lobbyNPC) {
        this.lobbyNPC = lobbyNPC;
        hologramEditor.select(lobbyNPC.getHologram());
    }

    public Pair<EditError, String> remove() {
        if (lobbyNPC == null) {
            return Pair.of(EditError.NPC_NOT_SELECTED, null);
        }

        String id = lobbyNPC.getId();

        this.lobbyNPC = null;
        hologramEditor.remove();

        return Pair.of(EditError.NONE, id);
    }

    public EditError setLocation(Location location) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        lobbyNPC.setLocation(location);
        hologramEditor.teleport(location.clone().add(0, 1, 0));
        return EditError.NONE;
    }

    public EditError setOption(String optionKey, Object optionValue) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        lobbyNPC.getOptions().put(optionKey, optionValue);
        return EditError.NONE;
    }

    public EditError setType(LobbyNPC.NPCType type, Object value) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        if (type == null) return EditError.ILLEGAL_TYPE;

        LobbyNPC.NPCType currentType = lobbyNPC.getType();

        lobbyNPC.setType(type);

        switch (type) {
            case PLAYER:
                break;
            case MOB:
                lobbyNPC.getOptions().put("entity_type", value != null ? value.toString() : "PIG");
                break;
            case ITEM_DISPLAY:
                if (!(value instanceof Item)) {
                    lobbyNPC.setType(currentType);
                    return EditError.ILLEGAL_TYPE;
                }
                lobbyNPC.getOptions().put("item", value.toString());
                break;
        }

        return EditError.NONE;
    }

    public EditError setSkin(Skin skin) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        if (lobbyNPC.getType() != LobbyNPC.NPCType.PLAYER) {
            return EditError.ILLEGAL_TYPE;
        }

        lobbyNPC.setSkin(skin);
        return EditError.NONE;
    }

    public EditError equipItem(EquipmentSlot slot, Item item) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        lobbyNPC.getEquipment().put(slot, item);
        return EditError.NONE;
    }

    public EditError addAction(String action) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        lobbyNPC.getClickActions().add(action);
        return EditError.NONE;
    }

    public EditError editAction(int actionIndex, @NotNull String newAction) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }
        if (actionIndex < 0 || actionIndex >= lobbyNPC.getClickActions().size()) {
            return EditError.INVALID_ACTION_INDEX;
        }

        lobbyNPC.getClickActions().set(actionIndex, newAction);
        return EditError.NONE;
    }

    public EditError removeAction(int actionIndex) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }
        if (actionIndex < 0 || actionIndex >= lobbyNPC.getClickActions().size()) {
            return EditError.INVALID_ACTION_INDEX;
        }

        lobbyNPC.getClickActions().remove(actionIndex);
        return EditError.NONE;
    }

    public List<String> getActions() {
        if (lobbyNPC == null) return Collections.emptyList();

        return lobbyNPC.getClickActions();
    }

    public EditError setGlow(boolean glow) {
        if (lobbyNPC == null) {
            return EditError.NPC_NOT_SELECTED;
        }

        lobbyNPC.getOptions().put("glow", glow);
        return EditError.NONE;
    }


    public enum EditError {
        NONE,
        NPC_NOT_SELECTED,
        INVALID_ACTION_INDEX,
        ILLEGAL_TYPE
    }



}
