package me.minimize.bundleplugin.config;

import me.minimize.bundleplugin.BundlePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PurchasedDataConfiguration {

    private final BundlePlugin plugin;
    private File file;
    private FileConfiguration data;

    public PurchasedDataConfiguration(BundlePlugin plugin) {
        this.plugin = plugin;
        reloadData();
    }

    public void reloadData() {
        file = new File(plugin.getDataFolder(), "purchased.yml");
        if (!file.exists()) {
            plugin.saveResource("purchased.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    public void saveData() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save purchased.yml!");
            e.printStackTrace();
        }
    }

    public List<String> getPurchasedBundles(UUID uuid) {
        return data.getStringList("purchased." + uuid.toString());
    }

    public void addPurchasedBundle(UUID uuid, String bundleId) {
        List<String> purchased = getPurchasedBundles(uuid);
        if (!purchased.contains(bundleId)) {
            purchased.add(bundleId);
            data.set("purchased." + uuid.toString(), purchased);
            saveData();
        }
    }

    public boolean hasPurchasedBundle(UUID uuid, String bundleId) {
        return getPurchasedBundles(uuid).contains(bundleId);
    }
}
