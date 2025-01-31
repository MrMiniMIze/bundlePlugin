package me.minimize.bundleplugin.managers;

import me.minimize.bundleplugin.BundlePlugin;
import me.minimize.bundleplugin.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Level;

/**
 * Loads and stores all bundles from bundles.yml
 */
public class BundleManager {

    private final BundlePlugin plugin;
    private final Map<String, BundleInfo> bundles = new HashMap<>();

    public BundleManager(BundlePlugin plugin) {
        this.plugin = plugin;
        loadBundles();
    }

    public void reloadBundles() {
        loadBundles();
    }

    public Map<String, BundleInfo> getBundlesMap() {
        return bundles;
    }

    public BundleInfo getBundle(String bundleId) {
        return bundles.get(bundleId);
    }

    private void loadBundles() {
        bundles.clear();
        FileConfiguration loaded = plugin.getBundleConfigurationFromFile("bundles.yml");
        if (loaded == null) {
            plugin.getLogger().severe("Could not load bundles.yml from data folder!");
            return;
        }

        ConfigurationSection section = loaded.getConfigurationSection("bundles");
        if (section == null) {
            plugin.getLogger().warning("No 'bundles' section found in bundles.yml!");
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection cs = section.getConfigurationSection(key);
            if (cs == null) continue;

            double price = cs.getDouble("price", 0);
            String unlock = cs.getString("unlock", "");

            // Start/end times
            String startStr = cs.getString("start", "");
            String endStr   = cs.getString("end", "");
            long startMillis = TimeUtils.parseDateTimeInPST(startStr, true);
            long endMillis   = TimeUtils.parseDateTimeInPST(endStr, false);

            // Display info for main menu
            ConfigurationSection displaySec = cs.getConfigurationSection("display");
            String displayMat  = "STONE";
            String displayName = "&fUntitled";
            List<String> displayLore = new ArrayList<>();

            if (displaySec != null) {
                displayMat  = displaySec.getString("material", "STONE");
                displayName = displaySec.getString("name", "&fUntitled");
                displayLore = displaySec.getStringList("lore");
            }

            // Content items
            List<BundleItem> contentItems = new ArrayList<>();
            if (cs.isConfigurationSection("items")) {
                ConfigurationSection itemsSec = cs.getConfigurationSection("items");
                for (String itemKey : itemsSec.getKeys(false)) {
                    ConfigurationSection itemCS = itemsSec.getConfigurationSection(itemKey);
                    if (itemCS == null) continue;

                    String matStr = itemCS.getString("material", "BOOK");
                    Material mat = Material.BOOK;
                    try {
                        mat = Material.valueOf(matStr.toUpperCase());
                    } catch (Exception ignored) {}

                    String name = itemCS.getString("name", "&eItem");
                    List<String> lore = itemCS.getStringList("lore");
                    List<String> commands = itemCS.getStringList("commands");

                    BundleItem bItem = new BundleItem(mat, name, lore, commands);
                    contentItems.add(bItem);
                }
            } else if (cs.isList("items")) {
                // If user uses a list, we can parse it directly
                // But let's keep the "map" approach for clarity.
                // We'll ignore the case of a direct list for brevity.
            }

            BundleInfo info = new BundleInfo(
                    key,
                    price,
                    unlock,
                    startMillis,
                    endMillis,
                    displayMat,
                    displayName,
                    displayLore,
                    contentItems
            );
            bundles.put(key, info);
            plugin.getLogger().log(Level.INFO, "Loaded bundle: " + key);
        }
    }
}
