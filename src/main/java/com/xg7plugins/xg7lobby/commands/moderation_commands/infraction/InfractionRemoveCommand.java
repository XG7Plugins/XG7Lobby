package com.xg7plugins.xg7lobby.commands.moderation_commands.infraction;

import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.modules.xg7menus.item.Item;
import org.bukkit.command.CommandSender;

public class InfractionRemoveCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        Command.super.onCommand(sender, args);
    }

    @Override
    public Item getIcon() {
        return null;
    }
}
