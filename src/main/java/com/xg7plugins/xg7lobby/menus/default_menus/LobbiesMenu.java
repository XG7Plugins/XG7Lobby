package com.xg7plugins.xg7lobby.menus.default_menus;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbiesMenu extends PagedMenu {
    public LobbiesMenu() {
        super(
                MenuConfigurations.of(
                        XG7LobbyLoader.getInstance(),
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
            if (!player.hasPermission("xg7lobby.command.lobby.delete"))
                lore.add("lang:[lobbies-menu.lobby-item.click-to-tp]");
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

            pagedItems.add(lobbyItem.clickable(event -> {
                Player p = event.getHolder().getPlayer();

                if (p.hasPermission("xg7lobby.command.lobby.delete") && event.getMenuAction().isRightClick()) {
                    p.closeInventory();
                    p.performCommand("deletelobby " + lobby.getID());
                    return;
                }

                p.getServer().dispatchCommand(p.getServer().getConsoleSender(), "lobby " + lobby.getID() + " " + p.getName());
            }));
        });

        return pagedItems;
    }

    @Override
    public List<Item> getItems(Player player) {
        return Arrays.asList(
                ChangePageItem.previousPageItem().name("lang:[lobbies-menu.go-back]").slot(45),
                CloseInventoryItem.get().name("lang:[lobbies-menu.close]").slot(49),
                ChangePageItem.nextPageItem().name("lang:[lobbies-menu.go-next]").slot(53)
        );
    }
}
