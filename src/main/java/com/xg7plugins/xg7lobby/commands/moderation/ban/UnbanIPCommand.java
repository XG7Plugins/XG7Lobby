package com.xg7plugins.xg7lobby.commands.moderation.ban;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "unbanip",
        description = "Unbans a player by ip",
        syntax = "/7lunbanip <ip>",
        permission = "xg7lobby.moderation.unban",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.EMERALD
)
public class UnbanIPCommand implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String ip = args.get(0, String.class);

        if (!Bukkit.getServer().getBanList(org.bukkit.BanList.Type.IP).isBanned(ip)) {
            return CommandState.error(XG7Lobby.getInstance(), "not-banned");
        }

        Bukkit.getServer().getBanList(org.bukkit.BanList.Type.IP).pardon(ip);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unban.on-unban", Pair.of("ip", ip));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? Bukkit.getBanList(org.bukkit.BanList.Type.IP).getBanEntries().stream().map(BanEntry::getTarget).collect(Collectors.toList()) : new ArrayList<>();
    }

}