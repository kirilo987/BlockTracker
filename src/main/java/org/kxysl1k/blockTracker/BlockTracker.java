package org.kxysl1k.blockTracker;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import java.util.List;

public class BlockTracker extends JavaPlugin implements Listener {
    private CoreProtectAPI coreProtect;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        coreProtect = getCoreProtect();
        if (coreProtect == null) {
            getLogger().warning("CoreProtect не знайдено! Плагін вимкнеться.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || event.getItem().getType() != Material.STICK) return;

        Player player = event.getPlayer();
        if (coreProtect != null) {
            List<String[]> lookup = coreProtect.blockLookup(event.getClickedBlock(), 1);
            if (lookup != null && !lookup.isEmpty()) {
                String owner = lookup.get(0)[0]; // Отримання імені гравця
                Player target = Bukkit.getPlayer(owner);
                if (target != null) {
                    highlightPlayer(target);
                    player.sendMessage(ChatColor.GREEN + "Цей блок був поставлений гравцем: " + ChatColor.AQUA + owner);
                } else {
                    player.sendMessage(ChatColor.RED + "Гравець, який поставив цей блок, не в мережі.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Дані про блок не знайдено.");
            }
        }
    }

    private void highlightPlayer(Player player) {
        Team team = player.getScoreboard().registerNewTeam("highlight");
        team.setColor(ChatColor.YELLOW);
        team.addEntry(player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                team.unregister();
            }
        }.runTaskLater(this, 100L); // Підсвічування на 5 секунд
    }

    private CoreProtectAPI getCoreProtect() {
        CoreProtect cp = (CoreProtect) getServer().getPluginManager().getPlugin("CoreProtect");
        return (cp != null) ? cp.getAPI() : null;
    }
}
