package me.minimize.bundleplugin.managers;

import java.util.List;

/**
 * Stores overall bundle data:
 * - price, unlock requirement, start/end timestamps
 * - display item (material, name, lore)
 * - content items (BundleItem objects)
 */
public class BundleInfo {

    private final String id;
    private final double price;
    private final String unlockRequirement;

    private final long startTimestamp;
    private final long endTimestamp;

    // For the main menu icon
    private final String displayMaterial;
    private final String displayName;
    private final List<String> displayLore;

    // The content items that appear in the BundleContentMenu
    private final List<BundleItem> contentItems;

    public BundleInfo(String id,
                      double price,
                      String unlockRequirement,
                      long startTimestamp,
                      long endTimestamp,
                      String displayMaterial,
                      String displayName,
                      List<String> displayLore,
                      List<BundleItem> contentItems) {
        this.id = id;
        this.price = price;
        this.unlockRequirement = unlockRequirement;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.displayMaterial = displayMaterial;
        this.displayName = displayName;
        this.displayLore = displayLore;
        this.contentItems = contentItems;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getUnlockRequirement() {
        return unlockRequirement;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public boolean isNotStarted() {
        return System.currentTimeMillis() < startTimestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTimestamp;
    }

    public String getDisplayMaterial() {
        return displayMaterial;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getDisplayLore() {
        return displayLore;
    }

    public List<BundleItem> getContentItems() {
        return contentItems;
    }
}
