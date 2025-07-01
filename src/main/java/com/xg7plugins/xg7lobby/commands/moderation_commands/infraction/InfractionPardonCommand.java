package com.xg7plugins.xg7lobby.commands.moderation_commands.infraction;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        String id = args.get(0, String.class);

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        Infraction infraction = playerManager.getInfraction(id);

        if (infraction == null) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.infraction-not-found");
            return;
        }

        playerManager.deleteInfraction(infraction);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.infraction.on-pardon", Pair.of("id", id));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.RED_WOOL, this);
    }
}
