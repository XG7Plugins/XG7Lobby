package com.xg7plugins.xg7lobby.help.gui;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.BookItem;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.item.SkullItem;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XG7LobbyHelpGUI extends Menu {
    public XG7LobbyHelpGUI() {
        super(MenuConfigurations.of(
                XG7Lobby.getInstance(),
                "xg7lobby-help",
                "lang:[help.menu.title]",
                6
        ));
    }

    @Override
    public List<Item> getItems(Player player) {
        SkullItem profileItem = (SkullItem) SkullItem.newSkull().renderPlayerSkull(true).name("lang:[help.menu.profile-item.name]");

        List<String> profileItemLore = new ArrayList<>();

        profileItemLore.add("lang:[help.menu.profile-item.lore.first-join]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.build-enabled]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.chat-locked]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.fly-enabled]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.kills]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.deaths]");
        profileItemLore.add("lang:[help.menu.profile-item.lore.kdr]");

        profileItem.lore(profileItemLore).slot(13);

        Item setLobbyItem = Item.from(XMaterial.COMPASS).name("lang:[help.menu.set-lobby-item.name]").slot(29);

        List<String> setLobbyItemLore = new ArrayList<>();

        setLobbyItemLore.add("lang:[help.menu.set-lobby-item.lore.current-location]");
        setLobbyItemLore.add("lang:[help.menu.set-lobby-item.lore.click]");

        setLobbyItem.lore(setLobbyItemLore);


        return Arrays.asList(
                profileItem,
                Item.from(XMaterial.matchXMaterial("COMMAND_BLOCK").orElse(XMaterial.ENDER_PEARL)).name("lang:[help.menu.commands-item.name]").lore("lang:[help.menu.commands-item.lore]").slot(28),
                setLobbyItem,
                Item.from(XMaterial.OAK_SIGN).name("lang:[help.menu.actions-item.name]").lore("lang:[help.menu.actions-item.lore]").slot(30),
                Item.from(XMaterial.BOOK).name("lang:[help.menu.menus-guide-item.name]").lore("lang:[help.menu.menus-guide-item.lore]").slot(32),
                Item.from(XMaterial.WRITABLE_BOOK).name("lang:[help.menu.about-item.name]").lore("lang:[help.menu.about-item.lore]").slot(33),
                Item.from(XMaterial.BLAZE_ROD).name("lang:[help.menu.custom-commands-guide-item.name]").lore("lang:[help.menu.custom-commands-guide-item.lore]").slot(34),
                Item.from(XMaterial.matchXMaterial("BARRIER").orElse(XMaterial.OAK_DOOR)).name("lang:[help.menu.close-item]").slot(45),
                Item.from(XMaterial.PAPER).name("lang:[help.menu.collaborators-item.name]").slot(53)
        );
    }

    @Override
    public void onClick(ActionEvent event) {
        Player player = event.getHolder().getPlayer();
        switch (event.getClickedSlot().get()) {
            case 45:
                player.closeInventory();
                break;
            case 28:
                XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("commands").open(player);
                break;
            case 29:
                player.performCommand("xg7lobby setlobby");
                BasicMenu.refresh(event.getHolder());
                break;
            case 30:
                XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("actions").open(player);
                break;
            case 32:
            case 33:
            case 34:
                Config lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Lobby.getInstance(), player).join().getLangConfiguration();

                List<String> about = lang.getList("help." + (event.getClickedSlot().equals(32) ? "menus-guide" : event.getClickedSlot().equals(33) ? "about" : "custom-commands-guide"), String.class).orElse(new ArrayList<>());

                BookItem bookItem = BookItem.newBook();

                List<List<String>> pages = new ArrayList<>();
                List<String> currentPage = new ArrayList<>();

                for (String line : about) {

                    currentPage.add(Text.detectLangs(player, XG7Plugins.getInstance(),line).join()
                            .replace("discord", "discord.gg/jfrn8w92kF")
                            .replace("github", "github.com/DaviXG7")
                            .replace("website", "xg7plugins.com")
                            .replace("version", XG7Lobby.getInstance().getDescription().getVersion())
                            .getText());
                    if (currentPage.size() == 10) {
                        pages.add(new ArrayList<>(currentPage));
                        currentPage.clear();
                    }
                }
                if (!currentPage.isEmpty()) {
                    pages.add(currentPage);
                }

                for (List<String> page : pages) {
                    bookItem.addPage(String.join("\n", page));
                }

                player.closeInventory();
                bookItem.openBook(player);
                break;
            case 53:
                XG7Lobby.getInstance().getHelpMessenger().getGui().getMenu("collaborators").open(player);
                break;
        }
    }
}
