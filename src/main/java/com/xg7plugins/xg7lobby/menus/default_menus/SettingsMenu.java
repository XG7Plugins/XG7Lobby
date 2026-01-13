package com.xg7plugins.xg7lobby.menus.default_menus;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbySettings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsMenu extends Menu {
    public SettingsMenu() {
        super(MenuConfigurations.of(
                XG7Lobby.getInstance(),
                "settings-menu",
                "lang:[settings-menu.title]",
                4
        ));
    }

    private static final int[][] SLOTS = {
            {},                     // 0
            {5},                    // 1
            {3, 7},                 // 2
            {3, 5, 7},              // 3
            {2, 4, 6, 8},           // 4
            {2, 4, 5, 6, 8}         // 5
    };


    @Override
    public List<InventoryItem> getItems(Player player) {

        LobbyPlayer lobbyPlayer = XG7Lobby.getAPI().getLobbyPlayer(player.getUniqueId());
        LobbySettings settings = lobbyPlayer.getLobbySettings();

        List<Pair<Boolean, Item>> items = new ArrayList<>();

        items.add(Pair.of(!settings.isHidingPlayers(), Item.from(XMaterial.ENDER_EYE)
                .name("lang:[settings-menu.toggle-visibility-item.name]")
                .lore(
                        "lang:[settings-menu.toggle-visibility-item.lore." + (settings.isHidingPlayers() ? "disabled" : "enabled") + "]",
                        "lang:[settings-menu.toggle-visibility-item.lore.click]"
                )
                .setNBTTag("type", "hide")
        ));

        items.add(Pair.of(!settings.isHidingChat(), Item.from(XMaterial.PAPER)
                .name("lang:[settings-menu.toggle-chat-messages-item.name]")
                .lore(
                        "lang:[settings-menu.toggle-chat-messages-item.lore." + (settings.isHidingChat() ? "disabled" : "enabled") + "]",
                        "lang:[settings-menu.toggle-chat-messages-item.lore.click]"
                )
                .setNBTTag("type", "chat")
        ));

        if (player.hasPermission("xg7lobby.fly")) {
            items.add(Pair.of(settings.isFlying(), Item.from(XMaterial.FEATHER)
                    .name("lang:[settings-menu.toggle-fly-item.name]")
                    .lore(
                            "lang:[settings-menu.toggle-fly-item.lore." + (settings.isFlying() ? "enabled" : "disabled") + "]",
                            "lang:[settings-menu.toggle-fly-item.lore.click]"
                    )
                            .setNBTTag("type", "fly")
                    )
            );
        }

        if (player.hasPermission("xg7lobby.build")) {
            items.add(Pair.of(settings.isBuildEnabled(), Item.from(XMaterial.DIAMOND_PICKAXE)
                    .name("lang:[settings-menu.toggle-build-item.name]")
                    .lore(
                            "lang:[settings-menu.toggle-build-item.lore." + (settings.isBuildEnabled() ? "enabled" : "disabled") + "]",
                            "lang:[settings-menu.toggle-build-item.lore.click]"
                    )
                            .setNBTTag("type", "build")
                    )
            );
        }


        try {
            return getInventoryItems(items);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<InventoryItem> getInventoryItems(List<Pair<Boolean, Item>> items) throws CloneNotSupportedException {

        List<InventoryItem> convertedItems = new ArrayList<>();

        int size = items.size();

        int[] cols = SLOTS[size];

        for (int i = 0; i < items.size(); i++) {
            Pair<Boolean, Item> item = items.get(i);

            convertedItems.add(item.getSecond().toInventoryItem(Slot.of(2, cols[i])));
            convertedItems.add(item.getSecond().clone()
                    .type(item.getFirst() ? XMaterial.LIME_DYE : XMaterial.GRAY_DYE)
                    .toInventoryItem(Slot.of(3, cols[i])));

        }

        return convertedItems;
    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);

        Item item = event.getClickedItem();

        if (item.isAir()) return;

        LobbyPlayer player = XG7Lobby.getAPI().getLobbyPlayer(event.getHolder().getPlayer().getUniqueId());

        InventoryUpdater updater = event.getHolder().getInventoryUpdater();

        item.getTag("type", String.class).ifPresent(type -> {
            switch (type) {
                case "hide":
                    player.getLobbySettings().setHidingPlayers(!player.getLobbySettings().isHidingPlayers());
                    break;
                case "chat":
                    player.getLobbySettings().setHidingChat(!player.getLobbySettings().isHidingChat());
                    break;
                case "fly":
                    player.getLobbySettings().setFlying(!player.getLobbySettings().isFlying());
                    player.fly();
                    break;
                case "build":
                    player.getLobbySettings().setBuildEnabled(!player.getLobbySettings().isBuildEnabled());
                    player.applyBuild();
                    break;
            }
            XG7Lobby.getAPI().lobbyPlayerManager().updatePlayerAsync(player).thenAccept(updated -> {
                if (updated) {
                    updater.clearInventory();
                    getItems(player.getPlayer()).forEach(updater::setItem);
                }
            });
        });



    }


}
