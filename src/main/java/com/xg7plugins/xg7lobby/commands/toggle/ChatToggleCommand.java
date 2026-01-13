package com.xg7plugins.xg7lobby.commands.toggle;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CommandSetup(
        name = "chat",
        description = "Toggles the chat visibility",
        syntax = "/chat (on/off)",
        permission = "xg7lobby.command.chat",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.ENDER_EYE
)
public class ChatToggleCommand implements Command {

    @CommandConfig(isPlayerOnly = true, isAsync = true)
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        Player player = (Player) sender;
        LobbyPlayer lobbyPlayer = XG7Lobby.getAPI().getLobbyPlayer(player.getUniqueId());

        boolean hide = !lobbyPlayer.getLobbySettings().isHidingChat();

        if (args.len() == 1) hide = args.get(0, String.class).equalsIgnoreCase("off");

        lobbyPlayer.getLobbySettings().setHidingChat(hide);

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), hide ? "chat.hidden" : "chat.shown");

        XG7Lobby.getAPI().lobbyPlayerManager().updatePlayer(lobbyPlayer);
        return CommandState.FINE;
    }

    @CommandConfig(
            name = "tell",
            description = "Sends a message to a player even if your chat is hidden",
            syntax = "/chat tell <player> <message>",
            permission = "xg7lobby.command.chat.tell",
            isPlayerOnly = true,
            isAsync = true,
            iconMaterial = XMaterial.PAPER
    )
    public CommandState tellerOnlyMessage(CommandSender sender, CommandArgs args) {

        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        OfflinePlayer player = args.get(0, OfflinePlayer.class);

        if (!player.isOnline()) return CommandState.NOT_ONLINE;

        String message = args.join(1);

        Text.send("§7§o" + sender.getName() + ": §r§o" + message, player.getPlayer());

        return CommandState.FINE;
    }

    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        if (args.len() == 1) return Arrays.asList("on", "off");
        return null;
    }
}
