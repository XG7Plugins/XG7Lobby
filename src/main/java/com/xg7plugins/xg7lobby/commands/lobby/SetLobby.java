package com.xg7plugins.xg7lobby.commands.lobby;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.uuid.ShortUUID;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.location.LobbyManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CommandSetup(
        name = "setlobby",
        description = "Sets a new lobby location",
        syntax = "/7lsetlobby (id) (On console: <id> ([world,x,y,z] or [world,x,y,z,yaw,pitch]))",
        permission = "xg7lobby.command.lobby.set",
        isInEnabledWorldOnly = true,
        pluginClass = XG7LobbyLoader.class
)
public class SetLobby implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7LobbyLoader.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        String id = null;

        if (args.len() > 0) id = args.get(0, String.class);
        else id = ShortUUID.generateUUID(10);

        Location location;

        if (!(sender instanceof Player)) {
            if (args.len() < 5) {
                return CommandState.syntaxError(getCommandSetup().syntax());
            }

            String world = args.get(1, String.class);
            double x = args.get(2, Double.class);
            double y = args.get(3, Double.class);
            double z = args.get(4, Double.class);

            float yaw = 0;
            float pitch = 0;

            if (args.len() == 7) {
                yaw = args.get(5, Float.class);
                pitch = args.get(6, Float.class);
            }

            location = Location.of(world, x, y, z, yaw, pitch);

        } else location = Location.fromPlayer((Player) sender);

        LobbyLocation lobbyLocation = new LobbyLocation(id, location, XG7Plugins.getAPI().getServerInfo());

        LobbyManager lobbyManager = XG7LobbyAPI.lobbyManager();

        try {
            String finalId = id;
            lobbyManager.saveLobbyLocation(lobbyLocation).thenRun(() -> {
                Text.sendTextFromLang(sender, XG7LobbyLoader.getInstance(), "lobby.on-set.on-success",
                        Pair.of("id", finalId),
                        Pair.of("world", location.getWorldName()),
                        Pair.of("x", String.format("%.2f", location.getX())),
                        Pair.of("y", String.format("%.2f", location.getY())),
                        Pair.of("z", String.format("%.2f", location.getZ())),
                        Pair.of("yaw", String.format("%.2f", location.getYaw())),
                        Pair.of("pitch", String.format("%.2f", location.getPitch()))
                );
            });
        } catch (ExecutionException | InterruptedException e) {
            Text.sendTextFromLang(sender, XG7LobbyLoader.getInstance(), "lobby.on-set.on-error", Pair.of("error", e.getMessage()));
            throw new RuntimeException(e);
        }

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() == 1) {
            return Collections.singletonList("id");
        }

        if (!(sender instanceof Player)) return Collections.emptyList();

        switch (args.len()) {
            case 2:
                return XG7Plugins.getAPI().getEnabledWorldsOf(XG7LobbyLoader.getInstance());
            case 3:
                return Collections.singletonList("x");
            case 4:
                return Collections.singletonList("y");
            case 5:
                return Collections.singletonList("z");
            case 6:
                return Collections.singletonList("yaw");
            case 7:
                return Collections.singletonList("pitch");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.BLAZE_ROD, this);
    }
}