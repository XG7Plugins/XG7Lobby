package com.xg7plugins.xg7lobby.menus.default_menus.infractions_menu;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7menus.Slot;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.editor.InventoryUpdater;
import com.xg7plugins.modules.xg7menus.events.ActionEvent;
import com.xg7plugins.modules.xg7menus.item.Item;

import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.menusimpl.Menu;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class InfractionsMenu extends Menu {

    private final Slot pos1;
    private final Slot pos2;

    public InfractionsMenu() {
        super(new InfractionsMenuConfiguration());

        pos1 = Slot.of(2,2);
        pos2 = Slot.of(5,8);

    }

    public List<Item> pagedItems(OfflinePlayer target) {

        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("moderation");

        LobbyPlayer lobbyPlayer = XG7LobbyAPI.getLobbyPlayer(target.getUniqueId());

        List<Item> items = new ArrayList<>();

        lobbyPlayer.getInfractions().forEach(infraction -> {

            Map warnLevel = config.getList("infraction-levels", Map.class).orElse(Collections.emptyList()).stream()
                    .filter(map -> map.get("level").equals(infraction.getLevel()))
                    .findFirst()
                    .orElse(new HashMap<>());

            String material = (String) warnLevel.get("menu-material");

            Item item = Item.from(material);

            item.name("lang:[warn-menu.warn-item.name]");

            List<String> lore = new ArrayList<>();

            lore.add("lang:[warn-menu.warn-item.reason]");
            lore.add("lang:[warn-menu.warn-item.date]");
            lore.add("lang:[warn-menu.warn-item.level]");
            lore.add("lang:[warn-menu.warn-item.click-to-copy-id]");

            item.lore(lore);

            item.setBuildPlaceholders(
                    Pair.of("target", target.getName()),
                    Pair.of("reason", infraction.getWarning()),
                    Pair.of("date", new SimpleDateFormat("dd/MM/yy HH:mm").format(infraction.getDate())),
                    Pair.of("level", String.valueOf(infraction.getLevel())),
                    Pair.of("id", String.valueOf(infraction.getID()))
            );

            item.setNBTTag("warn-id", String.valueOf(infraction.getID()));

            items.add(item);
        });

        return items;
    }

    @Override
    public List<Item> getItems(Player player) {
        return Arrays.asList(
                Item.from(Material.ARROW).name("lang:[warn-menu.go-back]").slot(45),
                Item.from(Material.BARRIER).name("lang:[warn-menu.close]").slot(49),
                Item.from(Material.ARROW).name("lang:[warn-menu.go-next]").slot(53)
        );
    }

    public boolean goPage(int page, InfractionsMenuHolder menuHolder) {

        List<Item> pagedItems = pagedItems(menuHolder.getTarget());

        if (page < 0) return false;
        if (page * Slot.areaOf(pos1, pos2) >= pagedItems.size()) return false;
        List<Item> itemsToAdd = pagedItems.subList(page * (Slot.areaOf(pos1, pos2)), pagedItems.size());


        XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Plugins.getInstance(), () -> {
            int index = 0;

            InventoryUpdater inventory = menuHolder.getInventoryUpdater();

            for (int x = pos1.getRow(); x <= pos2.getRow(); x++) {
                for (int y = pos1.getColumn(); y <= pos2.getColumn(); y++) {

                    if (index >= itemsToAdd.size()) {
                        if (inventory.hasItem(Slot.of(x, y))) inventory.setItem(Slot.of(x, y), Item.air());
                        continue;
                    }
                    inventory.setItem(Slot.of(x, y), itemsToAdd.get(index));

                    index++;
                }
            }

        }), 100L);
        return true;
    }

    public void open(Player player, OfflinePlayer target) {
        InfractionsMenuConfiguration config = (InfractionsMenuConfiguration) getMenuConfigs();
        config.setTarget(target);

        InfractionsMenuHolder menuHolder = new InfractionsMenuHolder(this, target, player);

        refresh(menuHolder);

        XG7Menus.registerHolder(menuHolder);
    }

    @Override
    public void onClick(ActionEvent event) {
        event.setCancelled(true);

        Player player = event.getHolder().getPlayer();

        InfractionsMenuHolder holder = (InfractionsMenuHolder) event.getHolder();

        if (event.getClickedItem().isAir()) return;

        switch (event.getClickedSlot().get()) {
            case 45:
                holder.previousPage();
                break;
            case 49:
                player.closeInventory();
                break;
            case 53:
                holder.nextPage();
                break;
            default:

                String id = event.getClickedItem().getTag("warn-id", String.class).orElse(null);

                Text.format(" ").send(player);

                Text.sendTextFromLang(player,XG7Lobby.getInstance(), "warn-menu.id-message", Pair.of("id", id));

                Text.format(" ").send(player);

        }

    }

    public static void refresh(InfractionsMenuHolder menuHolder) {
        BasicMenu.refresh(menuHolder);
        menuHolder.goPage(0);
    }
}
