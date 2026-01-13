package com.xg7plugins.xg7lobby.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CoreChecker {

    public static void check(JavaPlugin plugin) {

        if (Bukkit.getPluginManager().getPlugin("XG7Plugins") != null) {
            Bukkit.getLogger().info("Plugin (Core) found!");
            return;
        }

        Bukkit.getLogger().severe("XG7Plugins (Core) is not installed! Trying to install.");

        try {
            File pluginFile = new File(plugin.getDataFolder().getParent(), "XG7Plugins.jar");

            URL url = new URL("https://api.xg7plugins.com/plugins/download/1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(30_000);
            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Minecraft Plugin Downloader)"
            );

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error: " + responseCode);
            }

            int totalSize = connection.getContentLength();
            long downloaded = 0;

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(pluginFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                int lastPercent = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;

                    if (totalSize > 0) {
                        int percent = (int) ((downloaded * 100) / totalSize);

                        if (percent != lastPercent && percent % 10 == 0) {
                            Bukkit.getLogger().info("Downloading XG7Plugins: " + percent + "%");
                            lastPercent = percent;
                        }
                    }
                }
            }

            Bukkit.getLogger().info("");
            Bukkit.getLogger().info("XG7Plugins downloaded successfully. PLEASE RESTART THE SERVER TO AVOID ERRORS");
            Bukkit.getLogger().info("");
            Bukkit.getLogger().info("Trying to load XG7Plugins (Core)...");

            Bukkit.getPluginManager().loadPlugin(pluginFile);

        } catch (Exception e) {
            Bukkit.getLogger().severe(
                    "Failed to download XG7Plugins (Core). " +
                            "Please download manually from https://www.xg7plugins.com/pt?plugin=1&tab=0"
            );
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
}

