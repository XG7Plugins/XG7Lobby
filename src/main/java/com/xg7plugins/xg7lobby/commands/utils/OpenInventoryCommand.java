package com.xg7plugins.xg7lobby.commands.utils;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "openinv",
        permission = "xg7lobby.command.open",
        description = "Opens a custom inventory",
        syntax = "/7lopeninv <id>",
        isPlayerOnly = true,
        pluginClass = XG7Lobby.class
)
public class OpenInventoryCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.CHEST, this);
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        Player player = (Player) sender;

        String id = args.get(0, String.class);

        if (!XG7LobbyAPI.customInventoryManager().isMenu(id)) {
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "menu-does-not-exist");
            return CommandState.ERROR;
        }

        XG7LobbyAPI.customInventoryManager().openMenu(id, player);

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1) return new ArrayList<>(XG7LobbyAPI.customInventoryManager().getIds());
        return Collections.emptyList();
    }
}