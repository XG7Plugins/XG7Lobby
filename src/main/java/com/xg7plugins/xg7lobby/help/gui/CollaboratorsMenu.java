package com.xg7plugins.xg7lobby.help.gui;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.SkullItem;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.Action;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CollaboratorsMenu extends Menu {

    public CollaboratorsMenu() {
        super(MenuConfigurations.of(
                XG7Lobby.getInstance(),
                "collaborators-xg7lobby",
                "lang:[help.menu.collaborators-menu-title]",
                3
        ));
    }

    @Override
    public List<Item> getItems(Player player) {

        return Arrays.asList(
                SkullItem.newSkull().setPlayerSkinValue(UUID.fromString("45766b7f-9789-40e1-bd0b-46fa0d032bde")).name("&aDaviXG7").lore("&bCreator of all plugin").slot(11),
                SkullItem.newSkull().setPlayerSkinValue(UUID.fromString("f12b8505-8b77-4046-9d86-8b5303690096")).name("&aSadnessSad").lore("&bBeta tester and video helper").slot(12),
                SkullItem.newSkull().setPlayerSkinValue(UUID.fromString("696581df-4256-4028-b55e-9452b4de40b6")).name("&aBultzzXG7").lore("&bBeta tester and video helper").slot(13),
                SkullItem.newSkull().setPlayerSkinValue(UUID.fromString("f66d01bf-0e1c-4800-9a50-060411bff0bd")).name("&aMintNonExistent (Gorrfy)").lore("&bBeta tester").slot(14),
                SkullItem.newSkull().setPlayerSkinValue(UUID.fromString("35e9eeda-84ce-497d-af08-7cf5d68a21c7")).name("&aDanielXG7").lore("&bBeta tester").slot(15),
                Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[help.menu.close-item]").slot(18)
        );
    }

    public void onClick(ActionEvent event) {
        if (!event.getClickedSlot().equals(18)) return;
        XG7Lobby.getInstance().getHelpMessenger().sendGUI(event.getHolder().getPlayer());

    }

}
