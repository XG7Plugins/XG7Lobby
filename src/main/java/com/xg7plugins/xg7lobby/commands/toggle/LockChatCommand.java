package com.xg7plugins.xg7lobby.commands.toggle;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;

@CommandSetup(
        name = "lockchat",
        description = "Locks the chat",
        syntax = "/7llockchat",
        permission = "xg7lobby.command.lockchat",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.OAK_FENCE
)
public class LockChatCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @CommandConfig
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        ConfigSection data = ConfigFile.of("data/data", XG7Lobby.getInstance()).root();

        data.set("chat-locked", !data.get("chat-locked", false));
        data.getFile().save();

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "chat.on-" + (data.get("chat-locked", false) ? "lock" : "unlock"));

        return CommandState.FINE;
    }

}