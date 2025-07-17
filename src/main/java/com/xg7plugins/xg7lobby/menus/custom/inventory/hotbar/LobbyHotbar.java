package com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.player.PlayerMenuConfigurations;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Condition;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyInventory;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LobbyHotbar extends PlayerMenu implements LobbyInventory {

    @Getter
    private final Config menuConfig;

    private final Time cooldown;
    private final boolean disableCooldown;

    private final HashMap<String, LobbyItem> items;
    private final HashMap<Integer, String> grid;

    public LobbyHotbar(Config menuConfig, String id, HashMap<String, LobbyItem> items, HashMap<Integer, String> grid, Time cooldown, boolean disableCooldown) {
        super(PlayerMenuConfigurations.of(
                XG7Lobby.getInstance(),
                "lobby-custom-hotbar:" + id,
                EnumSet.allOf(MenuAction.class)
        ));

        this.items = items;
        this.grid = grid;

        this.cooldown = cooldown;
        this.disableCooldown = disableCooldown;

        this.menuConfig = menuConfig;
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
        if (!event.getMenuAction().isRightClick()) return;
        event.setCancelled(false);
        Item clickedItem = event.getClickedItem();
        if (clickedItem == null || clickedItem.getItemStack() == null || clickedItem.isAir()) return;

        if (XG7PluginsAPI.cooldowns().containsPlayer("selector-cooldown", event.getHolder().getPlayer())) {
            Text.sendTextFromLang(event.getHolder().getPlayer(), XG7Lobby.getInstance(), "selector-cooldown", Pair.of("time", XG7PluginsAPI.cooldowns().getReamingTime("selector-cooldown", event.getHolder().getPlayer()) + ""));
            return;
        }

        List<String> actions = (List<String>) clickedItem.getTag("actions", List.class).orElse(Collections.emptyList()).stream().map(action -> {
            if (action.toString().startsWith("[SWAP] ")) {
                return "[SWAP] " + getMenuConfigs().getId() + ", " + event.getClickedSlot().get() + ", " + action.toString().replace("[SWAP] ", "");
            }
            return action;
        }).collect(Collectors.toList());

        ActionsProcessor.process(actions, event.getHolder().getPlayer());

        if (!cooldown.isZero() && !disableCooldown) XG7PluginsAPI.cooldowns().addCooldown(event.getHolder().getPlayer(), "selector-cooldown", cooldown.getMilliseconds());
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

        if (!Condition.processConditions(lobbyItem.getConditionLine() + " Random_content", player).isEmpty()) {
            return lobbyItem;
        }
        if (lobbyItem.getOtherItemPath() == null) return null;

        return getItem(player, "items." + lobbyItem.getOtherItemPath());
    }
}
