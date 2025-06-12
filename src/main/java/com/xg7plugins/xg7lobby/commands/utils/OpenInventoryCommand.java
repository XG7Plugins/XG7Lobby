package com.xg7plugins.xg7lobby.commands.utils;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionType;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyInventory;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "open",
        permission = "xg7lobby.command.open",
        description = "Open the inventory",
        syntax = "/7lopen <id>",
        isPlayerOnly = true,
        pluginClass = XG7Lobby.class
)
public class OpenInventoryCommand implements Command {
    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.CHEST, this);
    }

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        Player player = (Player) sender;

        LobbyInventory lobbyInventory = XG7LobbyAPI.customInventoryManager().getInventory(args.get(0, String.class));

        if (lobbyInventory == null) {
            Text.sendTextFromLang(player,XG7Lobby.getInstance(), "menu-does-not-exist");
            return;
        }

        lobbyInventory.getMenu().open(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1) return new ArrayList<>(XG7LobbyAPI.customInventoryManager().getIds());
        return Collections.emptyList();
    }
}
