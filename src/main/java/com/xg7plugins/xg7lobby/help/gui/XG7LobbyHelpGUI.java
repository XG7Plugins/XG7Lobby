package com.xg7plugins.xg7lobby.help.gui;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;

import com.xg7plugins.modules.xg7menus.item.clickable.impl.CloseInventoryItem;
import com.xg7plugins.modules.xg7menus.item.clickable.impl.OpenBookClickableItem;
import com.xg7plugins.modules.xg7menus.item.impl.BookItem;
import com.xg7plugins.modules.xg7menus.item.impl.SkullItem;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XG7LobbyHelpGUI extends Menu {
    public XG7LobbyHelpGUI() {
        super(MenuConfigurations.of(
                XG7Lobby.getInstance(),
                "xg7lobby-help",
                "lang:[help.menu.title]",
                6,
                null,
                true,
                Collections.emptyList()
        ));
    }

    @Override
    public List<Item> getItems(Player player) {

        List<String> profileItemLore = new ArrayList<>();

        profileItemLore.add("lang:[help.menu.profile-item.lore.first-join]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.build-enabled]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.chat-locked]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.fly-enabled]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.kills]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.deaths]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.kdr]");

        ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Lobby.getInstance(), player).join().getSecond().getLangConfiguration();

        return Arrays.asList(
                SkullItem.newSkull().renderPlayerSkull(true)
                        .name("lang:[help.menu.profile-item.name]")
                        .lore(profileItemLore)
                        .slot(13),

                Item.from(XMaterial.matchXMaterial("COMMAND_BLOCK").orElse(XMaterial.ENDER_PEARL))
                        .name("lang:[help.menu.commands-item.name]")
                        .lore("lang:[help.menu.commands-item.lore]")
                        .slot(28)
                        .clickable(actionEvent -> XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("commands").open(player)),

                Item.from(XMaterial.COMPASS)
                        .name("lang:[help.menu.set-lobby-item.name]")
                        .lore("lang:[help.menu.set-lobby-item.lore.current-location]", "lang:[help.menu.set-lobby-item.lore.click]")
                        .setBuildPlaceholders(Pair.of("location", XG7LobbyAPI.lobbyManager().getRandomLobbyLocation().toString()))
                        .slot(29)
                        .clickable(actionEvent -> { player.performCommand("xg7lobby setlobby"); BasicMenu.refresh(actionEvent.getHolder()); }),

                Item.from(XMaterial.OAK_SIGN)
                        .name("lang:[help.menu.actions-item.name]")
                        .lore("lang:[help.menu.actions-item.lore]")
                        .slot(30)
                        .clickable(actionEvent -> XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("actions").open(player)),

                OpenBookClickableItem.get(
                        Item.from(XMaterial.BOOK).name("lang:[help.menu.menus-guide-item.name]").lore("lang:[help.menu.menus-guide-item.lore]"),
                        player,
                        lang.get("help.menus-guide")
                ).slot(32),

                OpenBookClickableItem.get(
                        Item.from(XMaterial.BOOK).name("lang:[help.menu.menus-guide-item.name]").lore("lang:[help.menu.menus-guide-item.lore]"),
                        player,
                        lang.get("help.menus-guide")
                ).slot(33),

                OpenBookClickableItem.get(
                        Item.from(XMaterial.WRITABLE_BOOK).name("lang:[help.menu.about-item.name]").lore("lang:[help.menu.about-item.lore]"),
                        player,
                        lang.get("help.about")
                ).slot(32),

                OpenBookClickableItem.get(
                        Item.from(XMaterial.BLAZE_ROD).name("lang:[help.menu.custom-commands-guide-item.name]").lore("lang:[help.menu.custom-commands-guide-item.lore]"),
                        player,
                        lang.get("help.custom-commands-guide")
                ).slot(34),

                CloseInventoryItem.get().slot(45),

                Item.from(XMaterial.PAPER)
                        .name("lang:[help.menu.collaborators-item.name]")
                        .slot(53)
                        .clickable(actionEvent -> XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("collaborators").open(player))
        );
    }
}
