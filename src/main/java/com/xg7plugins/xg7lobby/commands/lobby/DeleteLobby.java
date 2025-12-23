package com.xg7plugins.xg7lobby.commands.lobby;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "deletelobby",
        syntax = "/7ldeletelobby <id>",
        description = "Deletes a lobby",
        permission = "xg7lobby.command.lobby.delete",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.LAVA_BUCKET
)
public class DeleteLobby implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String id = args.get(0, String.class);

        XG7LobbyAPI.requestLobbyLocation(id).thenAccept(lobbyLocation -> {
            if (lobbyLocation == null) {
                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.delete.id-not-found", Pair.of("id", id));
                return;
            }

            XG7LobbyAPI.lobbyManager().deleteLobbyLocation(lobbyLocation)
                    .exceptionally(throwable -> {
                                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.delete.on-error", Pair.of("id", id));
                                throwable.printStackTrace();
                                return false;
                            }
                    ).thenRun(() -> Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "lobby.delete.on-success", Pair.of("id", id)));
        });

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return XG7Plugins.getAPI().database().getCachedEntities().asMap().join().values().stream().filter(ob -> ob instanceof LobbyLocation).map(e -> ((LobbyLocation) e).getID()).collect(Collectors.toList());
    }
}