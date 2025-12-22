package com.xg7plugins.xg7lobby.commands.moderation.infraction;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "infraction",
        description = "Manages the infractions of a player",
        permission = "xg7lobby.moderation.infraction.*",
        syntax = "/7linfraction <add|pardon>",
        pluginClass = XG7LobbyLoader.class
)
public class InfractionCommand implements Command {

    private final List<Command> subCommands = Arrays.asList(new InfractionAddCommand(), new InfractionPardonCommand());

    @Override
    public List<Command> getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) {
            return subCommands.stream().filter(cmd -> sender.hasPermission(cmd.getCommandSetup().permission())).map(cmd -> cmd.getCommandSetup().name()).collect(Collectors.toList());
        }

        if (args.get(0, String.class).equalsIgnoreCase("pardon") && args.len() == 2) {
            if (!sender.hasPermission("xg7lobby.command.infraction.pardon")) return Collections.emptyList();
            return XG7LobbyAPI.lobbyPlayerManager().getAllInfractions().stream().map(Infraction::getID).collect(Collectors.toList());
        }

        if (!sender.hasPermission("xg7lobby.command.infraction.add")) return Collections.emptyList();


        switch (args.len()) {
            case 2:
                return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
            case 3:
                return Arrays.asList("1", "2", "3", "level");
            case 4:
                return Collections.singletonList("reason");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.IRON_AXE, this);
    }
}
