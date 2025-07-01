package com.xg7plugins.xg7lobby.commands.toggle_commands;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.XG7LobbyConfig;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandSetup(
        name = "lockchat",
        description = "Locks the chat",
        syntax = "/7llockchat",
        permission = "xg7lobby.command.lockchat",
        pluginClass = XG7Lobby.class
)
public class LockChatCommand implements Command {
    @Override
    public void onCommand(CommandSender sender, CommandArgs args) {
        XG7LobbyConfig xg7LobbyConfig = XG7Lobby.getInstance().getEnvironmentConfig();

        xg7LobbyConfig.setChatLocked(!xg7LobbyConfig.isChatLocked());

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "chat.on-" + (xg7LobbyConfig.isChatLocked() ? "lock" : "unlock"));
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.OAK_FENCE, this);
    }
}
