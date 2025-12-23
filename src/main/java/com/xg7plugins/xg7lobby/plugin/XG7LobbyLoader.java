package com.xg7plugins.xg7lobby.plugin;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class XG7LobbyLoader extends JavaPlugin {

    @Getter
    private XG7Lobby plugin;

    private XG7Plugins corePlugin;

    @Override
    public void onLoad() {
        corePlugin = XG7Plugins.getInstance();
        plugin = new XG7Lobby(this);
        load(plugin);
    }

    @Override
    public void onEnable() {
        enable(plugin);
    }

    @Override
    public void onDisable() {
        disable(plugin);
    }

    public void load(Plugin plugin) {
        corePlugin.loadPlugin(plugin);
    }

    public void enable(Plugin plugin) {
        corePlugin.enablePlugin(plugin);
    }

    public void disable(Plugin plugin) {
        corePlugin.disablePlugin(plugin);
    }

    public static XG7Lobby getPluginInstance() {
        return getPlugin(XG7LobbyLoader.class).getPlugin();
    }
    
}
