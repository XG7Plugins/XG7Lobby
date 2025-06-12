package com.xg7plugins.xg7lobby.commands.toggle_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "fly",
        permission = "xg7lobby.command.fly",
        syntax = "/7lfly (player)",
        description = "Toggle fly mode",
        isInEnabledWorldOnly = true,
        pluginClass = XG7Lobby.class
)
public class FlyCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        OfflinePlayer target = null;
        boolean isOther = false;
        if (args.len() == 0) {
            if (!(sender instanceof Player)) {
                CommandMessages.NOT_A_PLAYER.send(sender);
                return;
            }
            target = (Player) sender;
        }

        if (args.len() > 0) {
            if (!sender.hasPermission("xg7lobby.command.fly-other")) {
                CommandMessages.NO_PERMISSION.send(sender);
                return;
            }
            target = args.get(0, OfflinePlayer.class);
            isOther = true;
        }
        if (isOther) {
            if (target == null || !target.hasPlayedBefore() || !target.isOnline()) {
                CommandMessages.PLAYER_NOT_FOUND.send(sender);
                return;
            }

            if (!XG7PluginsAPI.isInWorldEnabled(XG7Lobby.getInstance(), target.getPlayer())) {
                CommandMessages.DISABLED_WORLD.send(sender);
                return;
            }
        }

        boolean finalIsOther = isOther;

        OfflinePlayer finalTarget = target;
        XG7LobbyAPI.requestLobbyPlayer(target.getUniqueId()).thenAccept(lobbyPlayer -> {
            boolean before = lobbyPlayer.isFlying();

            lobbyPlayer.setFlying(!lobbyPlayer.isFlying());

            XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer).exceptionally(throwable -> {
                throwable.printStackTrace();
                lobbyPlayer.setFlying(before);
                XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
                return null;
            });

            if (finalTarget.isOnline()) {
                lobbyPlayer.fly();
                Text.sendTextFromLang(lobbyPlayer.getPlayer(),XG7Lobby.getInstance(), "commands.build." + (lobbyPlayer.isBuildEnabled() ? "toggle-on" : "toggle-off"));
            }
            if (finalIsOther) Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.build." + (lobbyPlayer.isBuildEnabled() ? "toggle-other-on" : "toggle-other-off"), Pair.of("target", lobbyPlayer.getPlayer().getDisplayName()));
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });


    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (!sender.hasPermission("xg7lobby.command.fly-other")) return Collections.emptyList();
        return Bukkit.getOnlinePlayers().stream().filter(player -> XG7PluginsAPI.isInWorldEnabled(XG7Lobby.getInstance(), player)).map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.FEATHER, this);
    }
}
