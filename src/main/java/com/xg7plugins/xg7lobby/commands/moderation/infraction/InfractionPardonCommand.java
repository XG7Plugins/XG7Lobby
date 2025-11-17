package com.xg7plugins.xg7lobby.commands.moderation.infraction;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "pardon",
        description = "Removes an infraction to a player",
        syntax = "/7linfraction pardon <infraction id>",
        pluginClass = XG7Lobby.class,
        permission = "xg7lobby.moderation.infraction.pardon",
        isAsync = true
)
public class InfractionPardonCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String id = args.get(0, String.class);

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        Infraction infraction = playerManager.getInfraction(id);

        if (infraction == null) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.infraction-not-found");
            return CommandState.ERROR;
        }

        playerManager.deleteInfraction(infraction);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.on-pardon", Pair.of("id", id));

        return CommandState.FINE;
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.RED_WOOL, this);
    }
}