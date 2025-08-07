package com.xg7plugins.xg7lobby.commands.lobby;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "deletelobby",
        syntax = "/7ldeletelobby <id>",
        description = "Deletes a lobby",
        permission = "xg7lobby.command.lobby.delete",
        pluginClass = XG7Lobby.class
)
public class DeleteLobby implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            Command.super.onCommand(sender, args);
            return;
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
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return XG7PluginsAPI.database().getCachedEntities().asMap().join().values().stream().filter(ob -> ob instanceof LobbyLocation).map(e -> ((LobbyLocation)e).getID()).collect(Collectors.toList());
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.LAVA_BUCKET, this);
    }
}
