package com.xg7plugins.xg7lobby.menus.custom.inventory.gui;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyGUI extends Menu implements LobbyInventory {

    @NotNull
    private final XMaterial fillItem;

    private final HashMap<String, LobbyItem> items;
    private final HashMap<Integer, String> grid;

    @Getter
    private final ConfigFile menuConfig;

    private final List<MenuAction> allowedActions;
    private final List<MenuAction> deniedActions;

    public LobbyGUI(ConfigFile menuConfig, String id, String title, int rows, @NotNull XMaterial fillItem, HashMap<String, LobbyItem> items, HashMap<Integer, String> grid, List<MenuAction> allowedActions, List<MenuAction> deniedActions, long updateInterval) {
        super(
                MenuConfigurations.of(
                        XG7Lobby.getInstance(),
                        "lobby-custom-menu:" + id,
                        title,
                        rows,
                        EnumSet.noneOf(MenuAction.class),
                        true,
                        Collections.emptyList(),
                        updateInterval
                )
        );

        this.items = items;
        this.grid = grid;

        this.fillItem = fillItem;

        this.menuConfig = menuConfig;
        this.allowedActions = allowedActions;
        this.deniedActions = deniedActions;
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
    public void onRepeatingUpdate(BasicMenuHolder holder) {

        InventoryUpdater updater = holder.getInventoryUpdater();

        for (int i = 0; i < 9; i++) {
            Item item  = updater.getItem(Slot.fromSlot(i));

            if (item.isAir()) {
                if (!grid.containsKey(i)) continue;

                String path = grid.get(i);

                LobbyItem lobbyItem = getItem(holder.getPlayer(), path);

                if (lobbyItem == null) continue;

                updater.addItem(lobbyItem.getItem().toInventoryItem(i));

                continue;
            }

            int finalI = i;
            item.getTag("lobby-item_id", String.class).ifPresent(id -> {
                LobbyItem lobbyItem = this.items.get(id);

                if (lobbyItem == null) {
                    updater.setItem(Slot.fromSlot(finalI), Item.from(Material.AIR));
                    return;
                }

                String path = grid.get(finalI);

                LobbyItem originalItem = getItem(holder.getPlayer(), path);

                if (originalItem == null) {
                    updater.setItem(Slot.fromSlot(finalI), Item.from(Material.AIR));
                    return;
                }

                updater.setItem(Slot.fromSlot(finalI), lobbyItem.getItem());
            });

        }

    }

    @Override
    public BasicMenu getMenu() {
        return this;
    }

    @Override
    public List<InventoryItem> getItems(Player player) {
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

        boolean shouldCancel = !allowedActions.contains(event.getMenuAction()) || deniedActions.contains(event.getMenuAction());

        event.setCancelled(shouldCancel);

        Item clickedItem = event.getClickedItem();
        if (clickedItem == null || clickedItem.getItemStack() == null || clickedItem.isAir()) return;

        LobbyItem lobbyItem = this.items.get(clickedItem.getTag("lobby-item_id", String.class).orElse(""));

        if (lobbyItem == null) return;

        boolean shouldCancelLobby;

        if (lobbyItem.getAllowedActions().isEmpty() && lobbyItem.getDeniedActions().isEmpty()) shouldCancelLobby = shouldCancel;
        else shouldCancelLobby = !lobbyItem.getAllowedActions().contains(event.getMenuAction()) || lobbyItem.getDeniedActions().contains(event.getMenuAction());

        event.setCancelled(shouldCancelLobby);

        @SuppressWarnings("unchecked")
        List<String> actions = (List<String>) clickedItem.getTag("lobby-item_actions", List.class).orElse(Collections.emptyList()).stream().map(action -> {
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
