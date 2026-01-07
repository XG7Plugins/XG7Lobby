package com.xg7plugins.xg7lobby.commands.utils;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "openform",
        permission = "xg7lobby.command.open",
        description = "Opens a custom geyser form",
        syntax = "/7lopenform <id>",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.CHEST
)
public class OpenFormCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig(isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        Player player = (Player) sender;

        String id = args.get(0, String.class);

        if (!XG7Lobby.getAPI().customFormsManager().contains(id)) {
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "form-does-not-exist");
            return CommandState.ERROR;
        }

        XG7Lobby.getAPI().customFormsManager().sendForm(id, player);

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1) return new ArrayList<>(XG7Lobby.getAPI().customFormsManager().getIds());
        return Collections.emptyList();
    }
}