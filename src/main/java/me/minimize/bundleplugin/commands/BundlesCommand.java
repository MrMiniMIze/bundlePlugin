package me.minimize.bundleplugin.commands;

import me.minimize.bundleplugin.BundlePlugin;
import me.minimize.bundleplugin.gui.BundleMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BundlesCommand implements CommandExecutor {

    private final BundlePlugin plugin;

    public BundlesCommand(BundlePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // /bundles reload
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bundleplugin.reload")) {
                sender.sendMessage(plugin.getMessagesConfiguration().getMessage("no-permission"));
                return true;
            }
            plugin.reloadAll();
            sender.sendMessage(plugin.getMessagesConfiguration().getMessage("reload-success"));
            return true;
        }

        // Must be player to open GUI
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessagesConfiguration().getMessage("only-player"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("bundleplugin.use")) {
            player.sendMessage(plugin.getMessagesConfiguration().getMessage("no-permission"));
            return true;
        }

        // Open the main Bundles GUI
        new BundleMenu(plugin, player).open();
        return true;
    }
}
