package org.kxysl1k.blockTracker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetWandCommand implements CommandExecutor {
    private final BlockTracker plugin;

    public GetWandCommand(BlockTracker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }
        Player player = (Player) sender;
        player.getInventory().addItem(plugin.createGlowingWand());
        player.sendMessage("§aYou received a magic wand!");
        return true;
    }
}