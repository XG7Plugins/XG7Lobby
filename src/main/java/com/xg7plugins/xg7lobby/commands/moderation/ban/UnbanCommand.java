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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommandSetup(
        name = "unban",
        description = "Unbans a player",
        syntax = "/7lunban <player>",
        permission = "xg7lobby.moderation.unban",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.EMERALD
)
public class UnbanCommand implements Command {

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        if (!target.isBanned()) {
            return CommandState.error(XG7Lobby.getInstance(), "not-banned");
        }

        Bukkit.getServer().getBanList(org.bukkit.BanList.Type.NAME).pardon(target.getName());

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unban.on-unban", Pair.of("target", target.getName()));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? Bukkit.getBanList(org.bukkit.BanList.Type.NAME).getBanEntries().stream().map(BanEntry::getTarget).collect(Collectors.toList()) : new ArrayList<>();
    }

}