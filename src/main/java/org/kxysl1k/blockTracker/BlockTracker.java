package org.kxysl1k.blockTracker;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class BlockTracker extends JavaPlugin implements Listener {
    private CoreProtectAPI coreProtect;
    private final NamespacedKey selectedBlockKey = new NamespacedKey(this, "selected_block");
    private final NamespacedKey wandKey = new NamespacedKey(this, "wand_item");
    private final int CUSTOM_MODEL_DATA = 1001; // Unique texture ID

    @Override
    public void onEnable() {
        coreProtect = getCoreProtect();
        if (coreProtect == null) {
            getLogger().warning("CoreProtect не знайдено! Плагін буде вимкнено.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("getwand").setExecutor(new GetWandCommand(this));
        registerCraftingRecipe();
    }

    private CoreProtectAPI getCoreProtect() {
        CoreProtect plugin = (CoreProtect) Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }
        CoreProtectAPI api = plugin.getAPI();
        return api.isEnabled() ? api : null;
    }

    private void registerCraftingRecipe() {
        ItemStack wand = createGlowingWand();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "glowing_wand"), wand);
        recipe.addIngredient(Material.GLOW_INK_SAC);
        recipe.addIngredient(Material.GLOWSTONE_DUST);
        recipe.addIngredient(Material.BRUSH);
        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getRecipe().getResult().isSimilar(createGlowingWand())) {
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (isGlowingWand(item)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cЕмм... як ти має скрафтити чарівну палику з чарівної плачики?");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isGlowingWand(item)) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {
            Block block = event.getClickedBlock();
            if (block == null) return;
            Location blockLoc = block.getLocation();
            List<String[]> history = coreProtect.blockLookup(block, 1);

            if (history != null && !history.isEmpty()) {
                String lastUser = history.get(0)[1];
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                meta.getPersistentDataContainer().set(selectedBlockKey, PersistentDataType.STRING,
                        blockLoc.getBlockX() + "," + blockLoc.getBlockY() + "," + blockLoc.getBlockZ() + " by " + lastUser);
                updateWandLore(item, blockLoc, lastUser);
                item.setItemMeta(meta);
                player.sendMessage("§aВи вибрали блок на " + blockLoc.toVector());
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String storedData = item.getItemMeta().getPersistentDataContainer().get(selectedBlockKey, PersistentDataType.STRING);
            if (storedData == null) return;

            String[] dataParts = storedData.split(" by ");
            if (dataParts.length < 2) return;
            String lastUser = dataParts[1];

            Player target = Bukkit.getPlayer(lastUser);
            if (target != null && target.isOnline()) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
                player.sendMessage("§eГравець " + lastUser + " тепер світиться!");
            } else {
                player.sendMessage("§cЦей гравець не в мережі або не знайдений.");
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (isGlowingWand(item)) {
            String storedData = item.getItemMeta().getPersistentDataContainer().get(selectedBlockKey, PersistentDataType.STRING);
            if (storedData != null) {
                event.getPlayer().sendMessage("§bВибраний блок: " + storedData);
            }
        }
    }

    private boolean isGlowingWand(ItemStack item) {
        if (item == null || item.getType() != Material.BRUSH) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(wandKey, PersistentDataType.STRING) &&
                meta.hasCustomModelData() && meta.getCustomModelData() == CUSTOM_MODEL_DATA;
    }

    private void updateWandLore(ItemStack item, Location location, String player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.setLore(List.of("§eБлок: " + location.getBlock().getType(), "§7Координати: " + location.toVector(), "§bОстанній: " + player));
        item.setItemMeta(meta);
    }

    public ItemStack createGlowingWand() {
        ItemStack wand = new ItemStack(Material.BRUSH);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§eЧарівна паличка");
            meta.setLore(List.of("§7Використовуйте його, щоб вибрати блок!"));
            meta.getPersistentDataContainer().set(wandKey, PersistentDataType.STRING, "true");
            meta.setCustomModelData(CUSTOM_MODEL_DATA);
            wand.setItemMeta(meta);
        }
        return wand;
    }
}