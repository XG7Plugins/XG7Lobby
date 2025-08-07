package com.xg7plugins.xg7lobby.commands.moderation.ban;

import com.xg7plugins.libs.xseries.XMaterial;
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
        name = "unban",
        description = "Unbans a player",
        syntax = "/7lunban <player>",
        permission = "xg7lobby.moderation.unban",
        pluginClass = XG7Lobby.class
)
public class UnbanCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {

        if (args.len() != 1) {
            CommandMessages.SYNTAX_ERROR.send(sender, getCommandSetup().syntax());
            return;
        }

        OfflinePlayer target = args.get(0, OfflinePlayer.class);

        if (!target.isBanned()) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unban.not-banned");
            return;
        }

        Bukkit.getServer().getBanList(org.bukkit.BanList.Type.NAME).pardon(target.getName());

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.unban.on-unban", Pair.of("target", target.getName()));

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        return args.len() == 1 ? Bukkit.getBanList(org.bukkit.BanList.Type.NAME).getBanEntries().stream().map(BanEntry::getTarget).collect(Collectors.toList()) : new ArrayList<>();
    }
    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.EMERALD, this);
    }
}
