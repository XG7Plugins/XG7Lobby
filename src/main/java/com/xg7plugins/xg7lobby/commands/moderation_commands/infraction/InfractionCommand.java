package com.xg7plugins.xg7lobby.commands.moderation_commands.infraction;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandSetup(
        name = "infraction",
        description = "Manages the infractions of a player",
        permission = "xg7lobby.moderation.infraction.*",
        syntax = "/7linfraction <add|remove>",
        pluginClass = XG7Lobby.class
)
public class InfractionCommand implements Command {

    private final List<Command> subCommands = Arrays.asList(new InfractionAddCommand(), new InfractionRemoveCommand());

    @Override
    public List<Command> getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return Command.super.onTabComplete(sender, args);
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.IRON_AXE, this);
    }
}
