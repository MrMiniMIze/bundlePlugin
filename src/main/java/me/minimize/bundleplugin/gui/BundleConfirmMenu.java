package me.minimize.bundleplugin.gui;

import me.minimize.bundleplugin.BundlePlugin;
import me.minimize.bundleplugin.managers.BundleInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BundleConfirmMenu {

    private final BundlePlugin plugin;
    private final Player player;
    private final BundleInfo bundle;
    private Inventory inventory;

    public BundleConfirmMenu(BundlePlugin plugin, Player player, BundleInfo bundle) {
        this.plugin = plugin;
        this.player = player;
        this.bundle = bundle;
    }

    public void open() {
        ConfigurationSection confirmMenuSec = plugin.getBundleConfiguration().getConfirmMenuSection();
        String title = ChatColor.DARK_RED + "Confirm Purchase?";
        if (confirmMenuSec != null) {
            title = ChatColor.translateAlternateColorCodes('&',
                    confirmMenuSec.getString("title", "&cConfirm Purchase?"));
        }

        inventory = Bukkit.createInventory(null, 9, title);

        // Slot 3: confirm item
        ItemStack confirmItem = createConfirmItem();
        inventory.setItem(3, confirmItem);

        // Slot 5: cancel item
        ItemStack cancelItem = createCancelItem();
        inventory.setItem(5, cancelItem);

        // Store the pending bundle
        ConfirmStorage.setPendingBundle(player, bundle);

        player.openInventory(inventory);
    }

    private ItemStack createConfirmItem() {
        ConfigurationSection confirmMenuSec = plugin.getBundleConfiguration().getConfirmMenuSection();
        Material mat = Material.GREEN_WOOL;
        String name = "&aConfirm";
        if (confirmMenuSec != null) {
            try {
                mat = Material.valueOf(confirmMenuSec.getString("confirm-item.material", "GREEN_WOOL").toUpperCase());
            } catch (Exception ignored) {}
            name = confirmMenuSec.getString("confirm-item.name", "&aConfirm");
        }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCancelItem() {
        ConfigurationSection confirmMenuSec = plugin.getBundleConfiguration().getConfirmMenuSection();
        Material mat = Material.RED_WOOL;
        String name = "&cCancel";
        if (confirmMenuSec != null) {
            try {
                mat = Material.valueOf(confirmMenuSec.getString("cancel-item.material", "RED_WOOL").toUpperCase());
            } catch (Exception ignored) {}
            name = confirmMenuSec.getString("cancel-item.name", "&cCancel");
        }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    public BundleInfo getBundle() {
        return bundle;
    }
}
