package com.xg7plugins.xg7lobby.commands.custom;

import com.xg7plugins.boot.PluginSetup;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;

import java.util.HashMap;

public class CustomCommandManager implements Manager {

    private final CustomCommandExecutor  customCommandExecutor = new CustomCommandExecutor(this);

    private final HashMap<String, CustomCommand> commands = new HashMap<>();

    public void registerCommands() {
        ConfigSection config = ConfigFile.of("custom_commands", XG7Lobby.getInstance()).root();

        Debug.of(XG7Lobby.getInstance()).info("Registering custom commands");

        if (!config.get("enabled", false)) return;

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

        for (String path : config.child("custom-commands").getKeys(false)) {
            CustomCommand customCommand = new CustomCommand(path);
            PluginCommand pluginCommand = (PluginCommand) ReflectionClass.of(PluginCommand.class)
                    .getConstructor(String.class, org.bukkit.plugin.Plugin.class)
                    .newInstance(customCommand.getName(), XG7Lobby.getInstance())
                    .getObject();
            pluginCommand.setAliases(customCommand.getAliases());
            pluginCommand.setDescription(customCommand.getDescription());
            pluginCommand.setPermission(customCommand.getPermission());
            pluginCommand.setUsage(customCommand.getSyntax());
            pluginCommand.setExecutor(customCommandExecutor);
            commandMap.register(XG7Lobby.getInstance().getClass().getAnnotation(PluginSetup.class).mainCommandName(), pluginCommand);
            commands.put(customCommand.getName(), customCommand);

            Debug.of(XG7Lobby.getInstance()).info("Registered " + customCommand.getName() + " command with succes!");
        }
    }

    public CustomCommand getCommand(String name) {
        return commands.get(name);
    }

    public void reloadCommands() {

    }




}
