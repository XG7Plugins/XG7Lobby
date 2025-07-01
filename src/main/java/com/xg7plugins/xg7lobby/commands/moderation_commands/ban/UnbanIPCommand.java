package com.xg7plugins.xg7lobby.commands.moderation_commands.ban;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
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
        name = "unbanip",
        description = "Unbans a player by ip",
        syntax = "/7lunbanip <ip>",
        permission = "xg7lobby.moderation.unban",
        pluginClass = XG7Lobby.class
)
public class UnbanIPCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandConfigurations().syntax());
            return;
        }

        String ip = args.get(0, String.class);

        if (!Bukkit.getServer().getBanList(org.bukkit.BanList.Type.IP).isBanned(ip)) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unban.not-banned");
            return;
        }

        Bukkit.getServer().getBanList(org.bukkit.BanList.Type.IP).pardon(ip);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unban.on-unban", Pair.of("ip", ip));

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? Bukkit.getBanList(org.bukkit.BanList.Type.IP).getBanEntries().stream().map(BanEntry::getTarget).collect(Collectors.toList()) : new ArrayList<>();
    }
    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.EMERALD, this);
    }
}
