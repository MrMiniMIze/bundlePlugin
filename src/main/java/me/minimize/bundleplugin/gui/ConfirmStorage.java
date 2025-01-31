package me.minimize.bundleplugin.gui;

import me.minimize.bundleplugin.managers.BundleInfo;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple static storage for which bundle a player is confirming.
 */
public class ConfirmStorage {

    private static final Map<UUID, BundleInfo> pending = new HashMap<>();

    public static void setPendingBundle(Player player, BundleInfo bundle) {
        pending.put(player.getUniqueId(), bundle);
    }

    public static BundleInfo getPendingBundle(Player player) {
        return pending.get(player.getUniqueId());
    }

    public static void removePending(Player player) {
        pending.remove(player.getUniqueId());
    }
}
