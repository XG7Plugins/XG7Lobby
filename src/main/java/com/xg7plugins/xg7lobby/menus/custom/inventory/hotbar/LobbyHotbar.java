package com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyInventory;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LobbyHotbar extends PlayerMenu implements LobbyInventory {

    @Getter
    private final ConfigFile menuConfig;

    private final Time cooldown;
    private final boolean disableCooldown;

    private final HashMap<String, LobbyItem> items;
    private final HashMap<Integer, String> grid;

    private final List<MenuAction> allowedActions;
    private final List<MenuAction> deniedActions;

    public LobbyHotbar(ConfigFile menuConfig, String id, HashMap<String, LobbyItem> items, HashMap<Integer, String> grid, Time cooldown, boolean disableCooldown, List<MenuAction> allowedActions, List<MenuAction> deniedActions, long updateInterval) {
        super(PlayerMenuConfigurations.of(
                XG7Lobby.getInstance(),
                "lobby-custom-hotbar:" + id,
                EnumSet.noneOf(MenuAction.class),
                true,
                Collections.emptyList(),
                updateInterval
        ));

        this.items = items;
        this.grid = grid;

        this.cooldown = cooldown;
        this.disableCooldown = disableCooldown;

        this.menuConfig = menuConfig;

        this.allowedActions = allowedActions;
        this.deniedActions = deniedActions;

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

                updater.addItem(lobbyItem.getItem().slot(i));

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

                if (originalItem.getItem().getItemStack().isSimilar(lobbyItem.getItem().getItemStack())) return;

                updater.setItem(Slot.fromSlot(finalI), lobbyItem.getItem());
            });

        }

    }

    @Override
    public List<Item> getItems(Player player) {
        List<Item> invItems = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (!grid.containsKey(i)) continue;

            String path = grid.get(i);

            LobbyItem lobbyItem = getItem(player, path);

            if (lobbyItem == null) continue;

            invItems.add(lobbyItem.getItem().slot(i));
        }

        return invItems;
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
    public void onClick(ActionEvent event) {

        boolean shouldCancel = !allowedActions.contains(event.getMenuAction()) || deniedActions.contains(event.getMenuAction());
        event.setCancelled(shouldCancel);


        Item clickedItem = event.getClickedItem();
        if (clickedItem == null) return;
        if (clickedItem.getItemStack() == null) return;
        if (clickedItem.isAir()) return;

        String itemId = clickedItem.getTag("lobby-item_id", String.class).orElse("");

        LobbyItem lobbyItem = this.items.get(itemId);
        if (lobbyItem == null) return;

        boolean shouldCancelLobby;

        if (lobbyItem.getAllowedActions().isEmpty() && lobbyItem.getDeniedActions().isEmpty()) shouldCancelLobby = shouldCancel;
        else shouldCancelLobby = !lobbyItem.getAllowedActions().contains(event.getMenuAction()) || lobbyItem.getDeniedActions().contains(event.getMenuAction());

        shouldCancelLobby = shouldCancelLobby && !event.getHolder().getPlayer().hasPermission("xg7lobby.inv");

        event.setCancelled(shouldCancelLobby);

        if (event.getMenuAction().isPlayerInteract() && !event.getMenuAction().isRightClick()) return;
        if (event.getMenuAction().isMenuInteract() && !event.getMenuAction().isLeftClick()) return;

        if (XG7Plugins.getAPI().cooldowns().containsPlayer("selector-cooldown", event.getHolder().getPlayer())) {
            long time = XG7Plugins.getAPI().cooldowns().getReamingTime("selector-cooldown", event.getHolder().getPlayer());

            Text.sendTextFromLang(event.getHolder().getPlayer(), XG7Lobby.getInstance(), "selector-cooldown",
                    Pair.of("time", time + ""));
            return;
        }

        List<String> actions = (List<String>) clickedItem
                .getTag("lobby-item_actions", List.class)
                .orElse(Collections.emptyList())
                .stream()
                .map(action -> {
                    if (action.toString().startsWith("[SWAP] ")) {
                        String result = "[SWAP] " + getMenuConfigs().getId() + ", " + event.getClickedSlot().get() + ", "
                                + action.toString().replace("[SWAP] ", "");

                        return result;
                    }

                    return action.toString();
                })
                .collect(Collectors.toList());

        ActionsProcessor.process(actions, event.getHolder().getPlayer());

        if (!disableCooldown) {
            long cd = !cooldown.isZero() ? cooldown.toMilliseconds() : 300L;
            XG7Plugins.getAPI().cooldowns().addCooldown(event.getHolder().getPlayer(), "selector-cooldown", cd);

        }

    }

    @Override
    public void onDrop(ActionEvent event) {event.setCancelled(false);}
    @Override
    public void onPickup(ActionEvent event) {event.setCancelled(false);}

    @Override
    public void onBreakBlocks(ActionEvent event) {event.setCancelled(false);}
    @Override
    public void onPlaceBlocks(ActionEvent event) {event.setCancelled(false);}

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
