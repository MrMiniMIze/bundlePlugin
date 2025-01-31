package me.minimize.bundleplugin.config;

import me.minimize.bundleplugin.BundlePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Handles the plugin's config.yml.
 */
public class BundleConfiguration {

    private final BundlePlugin plugin;
    private FileConfiguration config;

    public BundleConfiguration(BundlePlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig(); // reloads config.yml
        this.config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public int getGuiRows() {
        return config.getInt("gui.rows", 3);
    }

    public ConfigurationSection getPurchaseButtonSection() {
        return config.getConfigurationSection("purchase-button");
    }

    public ConfigurationSection getConfirmMenuSection() {
        return config.getConfigurationSection("confirm-menu");
    }
}
