package com.xg7plugins.xg7lobby.commands.custom_commands;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class CustomCommandManager implements Manager {

    private final CustomCommandExecutor  customCommandExecutor = new CustomCommandExecutor(this);

    private final HashMap<String, CustomCommand> commands = new HashMap<>();

    public void registerCommands() {
        Config config = Config.of("custom-commands", XG7Lobby.getInstance());

        if (!config.get("enabled", Boolean.class).orElse(false)) return;

        CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

        for (String path : config.get("custom-commands", ConfigurationSection.class).orElse(null).getKeys(false)) {
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
            commandMap.register(customCommand.getName(), pluginCommand);
            commands.put(customCommand.getName(), customCommand);
        }
    }

    public CustomCommand getCommand(String name) {
        return commands.get(name);
    }

    public void unregisterCommands() {
        commands.keySet().forEach(name -> {
            CommandMap commandMap = ReflectionObject.of(Bukkit.getServer()).getField("commandMap");

            if (!(commandMap instanceof SimpleCommandMap)) {
                throw new IllegalArgumentException("CommandMap was modified but commandMap was not SimpleCommandMap");
            }

            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) commandMap;

            Map<String, Command> knownCommands = ReflectionObject.of(simpleCommandMap).getField("knownCommands");

            Command command = knownCommands.get(name);
            if (command == null) return;
            knownCommands.remove(name);
            for (String alias : command.getAliases()) knownCommands.remove(alias);
        });
        commands.clear();
    }

    public void reloadCommands() {
        unregisterCommands();
        registerCommands();
    }



}
