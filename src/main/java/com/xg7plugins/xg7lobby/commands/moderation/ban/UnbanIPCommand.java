package com.xg7plugins.xg7lobby.commands.moderation.ban;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
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
        pluginClass = XG7LobbyLoader.class
)
public class UnbanIPCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7LobbyLoader.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() != 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        String ip = args.get(0, String.class);

        if (!Bukkit.getServer().getBanList(org.bukkit.BanList.Type.IP).isBanned(ip)) {
            Text.sendTextFromLang(sender, XG7LobbyLoader.getInstance(), "commands.unban.not-banned");
            return CommandState.ERROR;
        }

        Bukkit.getServer().getBanList(org.bukkit.BanList.Type.IP).pardon(ip);

        Text.sendTextFromLang(sender, XG7LobbyLoader.getInstance(), "commands.unban.on-unban", Pair.of("ip", ip));

        return CommandState.FINE;
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