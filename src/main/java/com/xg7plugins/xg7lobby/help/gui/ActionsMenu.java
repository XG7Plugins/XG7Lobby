package com.xg7plugins.xg7lobby.help.gui;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.ChangePageItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.PagedMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PagedMenuHolder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.Action;
import com.xg7plugins.xg7lobby.acitons.ActionType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActionsMenu extends PagedMenu {
    public ActionsMenu() {
        super(
                MenuConfigurations.of(
                        XG7Lobby.getInstance(),
                        "actions-menu-help",
                        "lang:[help.menu.actions-menu.title]",
                        6
                ),
                Slot.of(2, 2), Slot.of(4, 8)

        );
    }

    @Override
    public List<Item> pagedItems(Player player) {
        return Arrays.stream(ActionType.values()).map(Action::actionItem).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems(Player player) {

        return Arrays.asList(
                CloseInventoryItem.get(XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("index")).name("lang:[lobbies-menu.close]").slot(45),
                new ChangePageItem(
                        Item.from("PLAYER_HEAD:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmM5OWFhNmZjMmVjY2UzNTY2NWQ5NDhhMDEzMjUxNTNmZTUzZmMxNzcxZmIyNzg0ZjU3OTY3ZjEwZTJkZGNmOCJ9fX0=").getItemStack(),
                        ChangePageItem.Orientation.PREVIOUS
                ).name("lang:[help.menu.go-back-item]").slot(52),
                new ChangePageItem(
                        Item.from("PLAYER_HEAD:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThhZTExYTljOTQwYzVhYzYyYjkwNTgzN2QyMTUzN2RiZTJmM2U1MDExZjBiYmJmZGMxMTIyNGI4NjAzZGJiZCJ9fX0=").getItemStack(),
                        ChangePageItem.Orientation.NEXT
                ).name("lang:[help.menu.go-back-item]").slot(53)
        );
    }
}
