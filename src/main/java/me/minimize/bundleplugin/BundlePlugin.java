package me.minimize.bundleplugin;

import me.minimize.bundleplugin.commands.BundlesCommand;
import me.minimize.bundleplugin.config.BundleConfiguration;
import me.minimize.bundleplugin.config.MessagesConfiguration;
import me.minimize.bundleplugin.config.PurchasedDataConfiguration;
import me.minimize.bundleplugin.gui.InventoryClickListener;
import me.minimize.bundleplugin.managers.BundleManager;
import me.minimize.bundleplugin.managers.PurchaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Main plugin class for the BundlePlugin.
 */
public class BundlePlugin extends JavaPlugin {

    private static BundlePlugin instance;

    private BundleConfiguration bundleConfiguration;
    private MessagesConfiguration messagesConfiguration;
    private PurchasedDataConfiguration purchasedDataConfiguration;

    private BundleManager bundleManager;
    private PurchaseManager purchaseManager;

    @Override
    public void onEnable() {
        instance = this;

        // Ensure plugin folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Save default config files if they don't exist
        saveDefaultConfig(); // config.yml
        saveResource("messages.yml", false);
        saveResource("bundles.yml", false);
        saveResource("purchased.yml", false);

        // Load configurations
        this.bundleConfiguration = new BundleConfiguration(this);
        this.messagesConfiguration = new MessagesConfiguration(this);
        this.purchasedDataConfiguration = new PurchasedDataConfiguration(this);

        // Initialize managers
        this.bundleManager = new BundleManager(this);
        this.purchaseManager = new PurchaseManager(this);

        // Register command
        if (getCommand("bundles") != null) {
            getCommand("bundles").setExecutor(new BundlesCommand(this));
        }

        // Register inventory click listener
        new InventoryClickListener(this);

        getLogger().info("BundlePlugin enabled!");
    }

    @Override
    public void onDisable() {
        // Save any updated data on shutdown
        purchasedDataConfiguration.saveData();
        getLogger().info("BundlePlugin disabled!");
    }

    /**
     * Utility to load a YAML file from the plugin data folder (e.g. bundles.yml).
     */
    public org.bukkit.configuration.file.FileConfiguration getBundleConfigurationFromFile(String fileName) {
        File f = new File(getDataFolder(), fileName);
        if (!f.exists()) {
            return null;
        }
        return org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(f);
    }

    /**
     * A method to reload all relevant configs and data.
     */
    public void reloadAll() {
        reloadConfig();
        bundleConfiguration.reload();
        messagesConfiguration.reload();
        purchasedDataConfiguration.reloadData();
        bundleManager.reloadBundles();
    }

    public static BundlePlugin getInstance() {
        return instance;
    }

    public BundleConfiguration getBundleConfiguration() {
        return bundleConfiguration;
    }

    public MessagesConfiguration getMessagesConfiguration() {
        return messagesConfiguration;
    }

    public PurchasedDataConfiguration getPurchasedDataConfiguration() {
        return purchasedDataConfiguration;
    }

    public BundleManager getBundleManager() {
        return bundleManager;
    }

    public PurchaseManager getPurchaseManager() {
        return purchaseManager;
    }
}
