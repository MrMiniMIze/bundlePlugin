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

import java.util.*;

public class BundleMenu {

    private final BundlePlugin plugin;
    private final Player player;
    private Inventory inventory;

    public BundleMenu(BundlePlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        int rows = plugin.getBundleConfiguration().getGuiRows();
        int size = rows * 9;
        inventory = Bukkit.createInventory(null, size, ChatColor.DARK_GRAY + "Bundles");

        // Filler item from config "gui.filler"
        ConfigurationSection guiSection = plugin.getBundleConfiguration().getConfig().getConfigurationSection("gui");
        ItemStack fillerItem = null;
        if (guiSection != null && guiSection.isConfigurationSection("filler")) {
            fillerItem = createFillerItem(guiSection.getConfigurationSection("filler"));
        }

        // Fill with filler
        if (fillerItem != null) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, fillerItem);
            }
        }

        // Put each bundle's display item
        BundleManager manager = plugin.getBundleManager();
        Collection<BundleInfo> allBundles = manager.getBundlesMap().values();
        int index = 0;
        for (BundleInfo bundle : allBundles) {
            if (index >= size) break;
            ItemStack icon = createBundleIcon(bundle);
            inventory.setItem(index, icon);
            index++;
        }

        player.openInventory(inventory);
    }

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
        List<String> finalLore = new ArrayList<>();
        for (String line : loreLines) {
            finalLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(finalLore);

        filler.setItemMeta(meta);
        return filler;
    }

    private ItemStack createBundleIcon(BundleInfo bundle) {
        // Use the display info
        Material mat = Material.STONE;
        try {
            mat = Material.valueOf(bundle.getDisplayMaterial().toUpperCase());
        } catch (Exception ignored) {}

        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&', bundle.getDisplayName());
        meta.setDisplayName(displayName);

        // Build lore
        List<String> lore = new ArrayList<>();
        for (String line : bundle.getDisplayLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        // Price line
        lore.add(ChatColor.GRAY + "Price: " + ChatColor.GOLD + bundle.getPrice());
        // Time-based lines
        if (bundle.isNotStarted()) {
            long diff = bundle.getStartTimestamp() - System.currentTimeMillis();
            lore.add(ChatColor.RED + plugin.getMessagesConfiguration().getMessage("bundle-not-started-lore"));
            lore.add(ChatColor.GRAY + plugin.getMessagesConfiguration().getMessage("bundle-starts-in-lore")
                    .replace("%time%", TimeUtils.formatDuration(diff)));
        } else if (bundle.isExpired()) {
            lore.add(ChatColor.RED + plugin.getMessagesConfiguration().getMessage("bundle-expired-lore"));
        } else {
            long remaining = bundle.getEndTimestamp() - System.currentTimeMillis();
            lore.add(ChatColor.GRAY + plugin.getMessagesConfiguration().getMessage("bundle-ends-in-lore")
                    .replace("%time%", TimeUtils.formatDuration(remaining)));
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }
}
