package com.xg7plugins.xg7lobby.menus.default_menus;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.editor.InventoryShaper;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.holders.PagedMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.menus.gui.menus.PagedMenu;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbiesMenu extends PagedMenu {
    public LobbiesMenu() {
        super(
                MenuConfigurations.of(
                        XG7Lobby.getInstance(),
                        "lobbies-menu",
                        "lang:[lobbies-menu.title]",
                        6
                ),
                Slot.of(2,2), Slot.of(5,6)
        );
    }

    @Override
    public List<Item> pagedItems(Player player) {
        List<Item> pagedItems = new ArrayList<>();

        List<LobbyLocation> lobbies = XG7LobbyAPI.requestAllLobbyLocations().join();

        lobbies.forEach(lobby -> {
            Item lobbyItem = Item.from(XMaterial.NETHER_STAR);

            lobbyItem.name("lang:[lobbies-menu.lobby-item.name]");

            List<String> lore = new ArrayList<>();

            lore.add("lang:[lobbies-menu.lobby-item.server]");
            lore.add("lang:[lobbies-menu.lobby-item.location]");
            if (!player.hasPermission("xg7lobby.command.lobby.delete")) lore.add("lang:[lobbies-menu.lobby-item.click-to-tp]");
            else {
                lore.add("lang:[lobbies-menu.lobby-item.for-admins.click-to-tp]");
                lore.add("lang:[lobbies-menu.lobby-item.for-admins.click-to-remove]");
            }

            lobbyItem.lore(lore);

            lobbyItem.setBuildPlaceholders(
                    Pair.of("server", lobby.getServerInfo().getName()),
                    Pair.of("id", lobby.getID()),
                    Pair.of("x", String.format("%.2f", lobby.getLocation().getX())),
                    Pair.of("y", String.format("%.2f", lobby.getLocation().getY())),
                    Pair.of("z", String.format("%.2f", lobby.getLocation().getZ())),
                    Pair.of("world", lobby.getLocation().getWorld().getName()),
                    Pair.of("yaw", String.format("%.2f", lobby.getLocation().getYaw())),
                    Pair.of("pitch", String.format("%.2f", lobby.getLocation().getPitch())
                    )
            );

            lobbyItem.setNBTTag("lobby-id", lobby.getID());

            pagedItems.add(lobbyItem);
        });

        return pagedItems;
    }

    @Override
    public List<Item> getItems(Player player) {

        InventoryShaper editor = new InventoryShaper(getMenuConfigs());

        editor.setItem(Slot.fromSlot(45), Item.from(XMaterial.ARROW).name("lang:[lobbies-menu.go-back]").slot(45));
        editor.setItem(Slot.fromSlot(49), Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[lobbies-menu.close]").slot(49));
        editor.setItem(Slot.fromSlot(53), Item.from(XMaterial.ARROW).name("lang:[lobbies-menu.go-next]").slot(53));

        return editor.getItems();
    }

    @Override
    public void onClick(ActionEvent event) {
        if (event.getClickedItem().isAir()) return;

        PagedMenuHolder menuHolder = (PagedMenuHolder) event.getHolder();

        Player player = menuHolder.getPlayer();

        switch (event.getClickedSlot().get()) {
            case 45:
                menuHolder.previousPage();
                break;
            case 49:
                player.closeInventory();
                break;
            case 53:
                menuHolder.nextPage();
                break;
            default:

                Item item = event.getClickedItem();

                if (!item.getTag("lobby-id",String.class).isPresent()) return;

                String lobbyId = item.getTag("lobby-id",String.class).get();

                if (player.hasPermission("xg7lobby.command.lobby.delete") && event.getMenuAction().isRightClick()) {
                    player.closeInventory();
                    player.performCommand("deletelobby " + lobbyId);
                    return;
                }

                player.getServer().dispatchCommand(player.getServer().getConsoleSender(), "lobby " + lobbyId + " " + player.getName());

        }
    }
}
