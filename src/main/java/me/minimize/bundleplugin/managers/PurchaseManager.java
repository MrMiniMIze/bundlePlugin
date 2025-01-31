package me.minimize.bundleplugin.managers;

import me.minimize.bundleplugin.BundlePlugin;
import me.rivaldev.credits.api.RivalCreditsAPI;  // <-- from your decompiled code
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Handles checking and deducting credits through RivalCredits,
 * and running the purchase logic for a given bundle.
 */
public class PurchaseManager {

    private final BundlePlugin plugin;

    public PurchaseManager(BundlePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempts to purchase a bundle for the player.
     * Returns null if successful, or a message from messages.yml if there's an error/warning.
     */
    public String purchaseBundle(Player player, BundleInfo bundle) {
        // Already purchased?
        if (plugin.getPurchasedDataConfiguration().hasPurchasedBundle(player.getUniqueId(), bundle.getId())) {
            String msg = plugin.getMessagesConfiguration().getMessage("already-purchased");
            return msg.replace("%bundle%", bundle.getId());
        }

        // Not started?
        if (bundle.isNotStarted()) {
            String msg = plugin.getMessagesConfiguration().getMessage("bundle-not-started");
            return msg.replace("%bundle%", bundle.getId());
        }

        // Expired?
        if (bundle.isExpired()) {
            String msg = plugin.getMessagesConfiguration().getMessage("bundle-expired");
            return msg.replace("%bundle%", bundle.getId());
        }

        // Unlock requirement
        if (bundle.getUnlockRequirement() != null && !bundle.getUnlockRequirement().isEmpty()) {
            boolean hasRequired = plugin.getPurchasedDataConfiguration()
                    .hasPurchasedBundle(player.getUniqueId(), bundle.getUnlockRequirement());
            if (!hasRequired) {
                String msg = plugin.getMessagesConfiguration().getMessage("bundle-locked");
                msg = msg.replace("%previous%", bundle.getUnlockRequirement())
                        .replace("%bundle%", bundle.getId());
                return msg;
            }
        }

        // Check credits
        double cost = bundle.getPrice();
        if (!hasEnoughCredits(player, cost)) {
            // Not enough money
            return plugin.getMessagesConfiguration().getMessage("no-credits");
        }

        // Deduct credits
        deductCredits(player, cost);

        // Mark purchased
        plugin.getPurchasedDataConfiguration().addPurchasedBundle(player.getUniqueId(), bundle.getId());

        // Execute commands for each content item
        for (BundleItem item : bundle.getContentItems()) {
            for (String cmd : item.getCommands()) {
                String finalCmd = cmd.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
            }
        }

        // Return success
        String successMsg = plugin.getMessagesConfiguration().getMessage("purchase-success");
        return successMsg.replace("%bundle%", bundle.getId());
    }

    /**
     * Check RivalCredits balance for the player (using the decompiled API).
     */
    private boolean hasEnoughCredits(Player player, double cost) {
        RivalCreditsAPI api = new RivalCreditsAPI();  // no static getInstance() in your code
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUniqueId());
        double balance = api.getBalance(offline);     // equivalent to getCredits(OfflinePlayer)
        return balance >= cost;
    }

    /**
     * Deduct credits from the RivalCredits account.
     */
    private void deductCredits(Player player, double cost) {
        RivalCreditsAPI api = new RivalCreditsAPI();
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUniqueId());
        // "removeCredits(...)" subtracts the given amount
        api.removeCredits(offline, cost);
    }
}
