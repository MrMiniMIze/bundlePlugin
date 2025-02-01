package me.minimize.bundleplugin.gui;

import me.minimize.bundleplugin.BundlePlugin;
import me.minimize.bundleplugin.managers.BundleInfo;
import me.minimize.bundleplugin.managers.BundleManager;
import me.minimize.bundleplugin.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BundleMenu {

    private final BundlePlugin plugin;
    private final Player player;
    private Inventory inventory;

    public BundleMenu(BundlePlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        // Grab all bundles
        BundleManager manager = plugin.getBundleManager();
        Collection<BundleInfo> allBundles = manager.getBundlesMap().values();
        int total = allBundles.size();

        // Title example: "X Bundles Available"
        String title = ChatColor.GRAY.toString() + total + " Bundles Available";

        // Determine inventory size from config (rows * 9)
        int rows = plugin.getBundleConfiguration().getGuiRows();
        int size = rows * 9;
        inventory = Bukkit.createInventory(null, size, title);

        // Load the "gui" section
        ConfigurationSection guiSection = plugin.getBundleConfiguration()
                .getConfig()
                .getConfigurationSection("gui");

        // 1) Place filler items
        if (guiSection != null && guiSection.isConfigurationSection("filler")) {
            placeFillerItems(guiSection.getConfigurationSection("filler"), size);
        }

        // 2) Place bundles in the configured slots
        List<Integer> bundleSlots = new ArrayList<>();
        if (guiSection != null && guiSection.isList("bundle-slots")) {
            bundleSlots = guiSection.getIntegerList("bundle-slots");
        }

        // Convert the bundle collection to a list so we can iterate in order
        List<BundleInfo> bundlesList = new ArrayList<>(allBundles);

        for (int i = 0; i < bundleSlots.size(); i++) {
            if (i >= bundlesList.size()) break;  // No more bundles to place
            int slot = bundleSlots.get(i);
            if (slot >= 0 && slot < size) {
                BundleInfo bundle = bundlesList.get(i);
                ItemStack icon = createBundleIcon(bundle);
                inventory.setItem(slot, icon);
            }
        }

        // Open for the player
        player.openInventory(inventory);
    }

    /**
     * Places a filler item in the specified slots, as defined by the config.
     */
    private void placeFillerItems(ConfigurationSection fillerSec, int inventorySize) {
        // Create the filler item
        ItemStack fillerItem = createFillerItem(fillerSec);
        // Read the "slots" array from config
        List<Integer> fillerSlots = fillerSec.getIntegerList("slots");
        for (int slot : fillerSlots) {
            if (slot >= 0 && slot < inventorySize) {
                inventory.setItem(slot, fillerItem);
            }
        }
    }

    /**
     * Creates the filler item from the config section (material, name, lore).
     */
    private ItemStack createFillerItem(ConfigurationSection fillerSec) {
        String matName = fillerSec.getString("material", "GRAY_STAINED_GLASS_PANE");
        Material mat = Material.GRAY_STAINED_GLASS_PANE;
        try {
            mat = Material.valueOf(matName.toUpperCase());
        } catch (Exception ignored) {}

        ItemStack filler = new ItemStack(mat);
        ItemMeta meta = filler.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', fillerSec.getString("name", "&7"));
        meta.setDisplayName(name);

        List<String> loreLines = fillerSec.getStringList("lore");
        if (loreLines == null) loreLines = new ArrayList<>();
        List<String> finalLore = new ArrayList<>();
        for (String line : loreLines) {
            finalLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(finalLore);

        filler.setItemMeta(meta);
        return filler;
    }

    /**
     * Creates the icon shown for each bundle (material, name, lore).
     */
    private ItemStack createBundleIcon(BundleInfo bundle) {
        // Try to parse the display material from the bundle
        Material mat = Material.STONE;
        try {
            mat = Material.valueOf(bundle.getDisplayMaterial().toUpperCase());
        } catch (Exception ignored) {}

        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();

        // Display name
        String displayName = ChatColor.translateAlternateColorCodes('&', bundle.getDisplayName());
        meta.setDisplayName(displayName);

        // Build lore from the bundle's displayLore
        List<String> lore = new ArrayList<>();
        for (String line : bundle.getDisplayLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        // Price
        lore.add(ChatColor.GRAY + "Price: " + ChatColor.GOLD + bundle.getPrice());

        // Time lines
        if (bundle.isNotStarted()) {
            long diff = bundle.getStartTimestamp() - System.currentTimeMillis();
            lore.add(ChatColor.RED + "Not available yet!");
            lore.add(ChatColor.GRAY + "Starts in: " + ChatColor.YELLOW + TimeUtils.formatDuration(diff));
        } else if (bundle.isExpired()) {
            lore.add(ChatColor.RED + "Expired");
        } else {
            long remaining = bundle.getEndTimestamp() - System.currentTimeMillis();
            lore.add(ChatColor.GRAY + "Ends in: " + ChatColor.YELLOW + TimeUtils.formatDuration(remaining));
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }
}
