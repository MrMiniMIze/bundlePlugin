package me.minimize.bundleplugin.gui;

import me.minimize.bundleplugin.BundlePlugin;
import me.minimize.bundleplugin.managers.BundleInfo;
import me.minimize.bundleplugin.managers.BundleItem;
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
import java.util.List;

/**
 * Shows the content items of a bundle, plus a purchase button at the bottom.
 */
public class BundleContentMenu {

    private final BundlePlugin plugin;
    private final Player player;
    private final BundleInfo bundle;
    private Inventory inventory;

    public BundleContentMenu(BundlePlugin plugin, Player player, BundleInfo bundle) {
        this.plugin = plugin;
        this.player = player;
        this.bundle = bundle;
    }

    public void open() {
        // We'll use a 27-slot inventory (3 rows)
        int size = 27;
        String title = ChatColor.DARK_GREEN + "Bundle: " + ChatColor.stripColor(bundle.getDisplayName());
        inventory = Bukkit.createInventory(null, size, title);

        // Show the content items (defined in bundles.yml -> items)
        List<BundleItem> itemList = bundle.getContentItems();
        int index = 0;
        for (BundleItem bItem : itemList) {
            if (index >= 18) break; // top 2 rows only
            inventory.setItem(index, createContentItem(bItem));
            index++;
        }

        // Purchase button in the bottom center
        int centerSlot = 22;
        inventory.setItem(centerSlot, createPurchaseButton());

        player.openInventory(inventory);
    }

    private ItemStack createContentItem(BundleItem bItem) {
        Material mat = Material.BOOK;
        try {
            mat = bItem.getMaterial();
        } catch (Exception ignored) {}

        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', bItem.getName()));
        List<String> lore = new ArrayList<>();
        for (String line : bItem.getLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }

    private ItemStack createPurchaseButton() {
        // from config.yml -> purchase-button section
        ConfigurationSection purchaseSec = plugin.getBundleConfiguration().getPurchaseButtonSection();

        Material mat = Material.EMERALD;
        String display = "&aPurchase Bundle";
        List<String> loreLines = new ArrayList<>();

        if (purchaseSec != null) {
            try {
                mat = Material.valueOf(purchaseSec.getString("material", "EMERALD").toUpperCase());
            } catch (Exception ignored) {}

            display = purchaseSec.getString("name", "&aPurchase Bundle");
            loreLines = purchaseSec.getStringList("lore");
        }

        ItemStack button = new ItemStack(mat);
        ItemMeta meta = button.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', display));

        List<String> finalLore = new ArrayList<>();
        for (String line : loreLines) {
            line = ChatColor.translateAlternateColorCodes('&', line);
            line = line.replace("%price%", String.valueOf(bundle.getPrice()));

            if (bundle.isNotStarted()) {
                long diff = bundle.getStartTimestamp() - System.currentTimeMillis();
                line = line.replace("%timeleft%", "Starts in " + TimeUtils.formatDuration(diff));
            } else if (bundle.isExpired()) {
                line = line.replace("%timeleft%", ChatColor.RED + "Expired");
            } else {
                long remaining = bundle.getEndTimestamp() - System.currentTimeMillis();
                line = line.replace("%timeleft%", TimeUtils.formatDuration(remaining));
            }

            finalLore.add(line);
        }

        meta.setLore(finalLore);
        button.setItemMeta(meta);

        return button;
    }
}
