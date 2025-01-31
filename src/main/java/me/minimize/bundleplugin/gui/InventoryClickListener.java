package me.minimize.bundleplugin.gui;

import me.minimize.bundleplugin.BundlePlugin;
import me.minimize.bundleplugin.managers.BundleInfo;
import me.minimize.bundleplugin.managers.PurchaseManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private final BundlePlugin plugin;

    public InventoryClickListener(BundlePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        // top inventory check
        boolean isTopInventory = event.getRawSlot() < event.getView().getTopInventory().getSize();

        // if click is in top inventory, block them from taking items
        if (isTopInventory) {
            event.setCancelled(true);
        }

        // also block shift-click from top
        if (event.isShiftClick() && isTopInventory) {
            event.setCancelled(true);
            return;
        }

        String title = ChatColor.stripColor(event.getView().getTitle());

        if (title.equalsIgnoreCase("Bundles")) {
            if (!isTopInventory) return; // let them use their own inventory
            if (isFiller(event.getCurrentItem())) return;

            int slot = event.getSlot();
            BundleInfo clickedBundle = plugin.getBundleManager()
                    .getBundlesMap()
                    .values()
                    .stream()
                    .skip(slot)
                    .findFirst()
                    .orElse(null);
            if (clickedBundle == null) return;

            // Check if started or expired
            if (clickedBundle.isNotStarted()) {
                player.sendMessage(plugin.getMessagesConfiguration().getMessage("not-available-yet"));
                return;
            }
            if (clickedBundle.isExpired()) {
                player.sendMessage(plugin.getMessagesConfiguration().getMessage("expired-bundle"));
                return;
            }

            new BundleContentMenu(plugin, player, clickedBundle).open();
        }
        else if (title.startsWith("Bundle:")) {
            if (!isTopInventory) return;
            int centerSlot = 22;
            if (event.getSlot() == centerSlot) {
                BundleInfo b = getBundleFromTitle(title);
                if (b == null) return;

                if (b.isNotStarted()) {
                    player.sendMessage(plugin.getMessagesConfiguration().getMessage("not-available-yet"));
                    return;
                }
                if (b.isExpired()) {
                    player.sendMessage(plugin.getMessagesConfiguration().getMessage("expired-bundle"));
                    return;
                }

                new BundleConfirmMenu(plugin, player, b).open();
            }
        }
        else if (title.contains("Confirm Purchase")) {
            if (!isTopInventory) return;
            int slot = event.getSlot();
            if (slot == 3) {
                // Confirm
                BundleInfo pending = ConfirmStorage.getPendingBundle(player);
                if (pending != null) {
                    PurchaseManager pm = plugin.getPurchaseManager();
                    String result = pm.purchaseBundle(player, pending);
                    if (result != null) {
                        player.sendMessage(result);
                    }
                    ConfirmStorage.removePending(player);
                }
                player.closeInventory();
            } else if (slot == 5) {
                // Cancel
                ConfirmStorage.removePending(player);
                player.closeInventory();
            }
        }
    }

    private boolean isFiller(org.bukkit.inventory.ItemStack item) {
        if (!item.hasItemMeta()) return false;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        return name.isEmpty();
    }

    private BundleInfo getBundleFromTitle(String title) {
        String rawName = title.replace("Bundle:", "").trim().toLowerCase().replaceAll("&", "");
        for (BundleInfo info : plugin.getBundleManager().getBundlesMap().values()) {
            String checkName = ChatColor.stripColor(info.getDisplayName()).toLowerCase().replaceAll("&", "");
            if (rawName.contains(checkName)) {
                return info;
            }
        }
        return null;
    }
}
