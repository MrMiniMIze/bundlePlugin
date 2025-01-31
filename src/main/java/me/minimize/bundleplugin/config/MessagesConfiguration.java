package me.minimize.bundleplugin.config;

import me.minimize.bundleplugin.BundlePlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesConfiguration {

    private final BundlePlugin plugin;
    private File file;
    private FileConfiguration messages;

    public MessagesConfiguration(BundlePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path, path));
    }
}
