package me.minimize.bundleplugin.managers;

import org.bukkit.Material;

import java.util.List;

/**
 * Represents a single content item in a bundle.
 * - material
 * - name
 * - lore
 * - commands to run when the bundle is purchased
 */
public class BundleItem {

    private final Material material;
    private final String name;
    private final List<String> lore;
    private final List<String> commands; // commands to run on purchase

    public BundleItem(Material material, String name, List<String> lore, List<String> commands) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.commands = commands;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getCommands() {
        return commands;
    }
}
