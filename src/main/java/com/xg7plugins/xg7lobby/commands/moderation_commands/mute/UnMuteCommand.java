package com.xg7plugins.xg7lobby.commands.moderation_commands.mute;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "unmute",
        description = "Unmutes a player",
        syntax = "/7plunmute <player>",
        permission = "xg7lobby.moderation.unmute",
        isAsync = true,
        pluginClass = XG7Lobby.class
)
public class UnMuteCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(target.getUniqueId());

        if (player == null) {
            CommandMessages.PLAYER_NOT_FOUND.send(sender);
            return;
        }

        if (!player.isMuted()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unmute.not-muted", Pair.of("target", target.getName()));
            return;
        }


        player.setMuted(false);
        player.setUnmuteTime(Time.of(0));
        XG7LobbyAPI.lobbyPlayerManager().updatePlayer(player);

        if (target.isOnline()) {
            Text.sendTextFromLang(target.getPlayer(),XG7Lobby.getInstance(), "commands.unmute.on-unmute");
        }

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unmute.on-unmute-sender", Pair.of("target", player.getOfflinePlayer().getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? new ArrayList<>(XG7PluginsAPI.getAllPlayerNames()) : Collections.emptyList();
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.IRON_DOOR, this);
    }

}
