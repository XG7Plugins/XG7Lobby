package com.xg7plugins.xg7lobby.menus.custom.inventory.gui;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.utils.text.Condition;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyInventory;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyGUI extends Menu implements LobbyInventory {

    @NotNull
    private final XMaterial fillItem;

    private final HashMap<String, LobbyItem> items;
    private final HashMap<Integer, String> grid;

    @Getter
    private final Config menuConfig;

    public LobbyGUI(Config menuConfig, String id, String title, int rows, @NotNull XMaterial fillItem, HashMap<String, LobbyItem> items, HashMap<Integer, String> grid) {
        super(
                MenuConfigurations.of(
                        XG7Lobby.getInstance(),
                        "lobby-custom-menu:" + id,
                        title,
                        rows
                )
        );

        this.items = items;
        this.grid = grid;

        this.fillItem = fillItem;

        this.menuConfig = menuConfig;
    }

    @Override
    public HashMap<String, LobbyItem> items() {
        return items;
    }

    @Override
    public HashMap<Integer, String> grid() {
        return grid;
    }

    @Override
    public BasicMenu getMenu() {
        return this;
    }

    @Override
    public List<Item> getItems(Player player) {
        InventoryShaper editor = new InventoryShaper(getMenuConfigs());

        Item fillItem = Item.from(this.fillItem);

        if (!fillItem.getItemStack().getType().equals(Material.AIR)) {
            fillItem.name(" ");
        }

        editor.fillInventory(fillItem);

        for (int i = 0; i < getMenuConfigs().getRows() * 9; i++) {
            if (grid.containsKey(i)) {
                String path = grid.get(i);

                LobbyItem lobbyItem = getItem(player, path);

                if (lobbyItem == null) continue;

                editor.setItem(Slot.fromSlot(i), lobbyItem.getItem());
            }
        }

        return editor.getItems();
    }

    @Override
    public void onClick(ActionEvent event) {
        Item clickedItem = event.getClickedItem();
        if (clickedItem == null || clickedItem.getItemStack() == null || clickedItem.isAir()) return;
        event.setCancelled(true);

        List<String> actions = (List<String>) clickedItem.getTag("actions", List.class).orElse(Collections.emptyList()).stream().map(action -> {
            if (action.toString().startsWith("[SWAP] ")) {
                return "[SWAP] " + getMenuConfigs().getId() + ", " + event.getClickedSlot().get() + ", " + action.toString().replace("[SWAP] ", "");
            }
            return action;
        }).collect(Collectors.toList());

        ActionsProcessor.process(actions, event.getHolder().getPlayer());
    }

    private LobbyItem getItem(Player player, String path) {
        LobbyItem lobbyItem = this.items.get(path);

        if (lobbyItem == null) return null;

        if (lobbyItem.getConditionLine() == null || lobbyItem.getConditionLine().isEmpty()) return lobbyItem;

        if (!Text.format(lobbyItem.getConditionLine() + "RANDOLAS").textFor(player).getPlainText().isEmpty()) {
            return lobbyItem;
        }
        if (lobbyItem.getOtherItemPath() == null) return null;

        return getItem(player, lobbyItem.getOtherItemPath());
    }

}
