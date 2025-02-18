package org.kxysl1k.blockTracker;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BlockTracker extends JavaPlugin implements Listener {

    private CoreProtectAPI coreProtect;
    private final Map<UUID, BlockData> selectedBlocks = new HashMap<>();

    @Override
    public void onEnable() {
        // Перевірка наявності CoreProtect
        coreProtect = getCoreProtect();
        if (coreProtect == null) {
            getLogger().severe("CoreProtect не знайдено! Вимкнення плагіна.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Реєстрація подій
        getServer().getPluginManager().registerEvents(this, this);

        // Додавання рецепту крафту спеціальної кісточки
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "special_bone"), createSpecialBone());
        recipe.shape(" I ", "IBI", " I ");
        recipe.setIngredient('I', Material.GLOW_INK_SAC);
        recipe.setIngredient('B', Material.BONE);
        getServer().addRecipe(recipe);

        getLogger().info("BlockTracker увімкнено!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockTracker вимкнено!");
    }

    // Отримання CoreProtect API
    private CoreProtectAPI getCoreProtect() {
        if (Bukkit.getPluginManager().getPlugin("CoreProtect") instanceof CoreProtect) {
            CoreProtect coreProtect = (CoreProtect) Bukkit.getPluginManager().getPlugin("CoreProtect");
            if (coreProtect.getAPI().APIVersion() == 6) {
                return coreProtect.getAPI();
            }
        }
        return null;
    }

    // Створення спеціальної кісточки
    private ItemStack createSpecialBone() {
        ItemStack bone = new ItemStack(Material.BONE);
        ItemMeta meta = bone.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "BlockTracker");
        bone.setItemMeta(meta);
        return bone;
    }

    // Оновлення опису кісточки
    private void updateBoneLore(ItemStack bone, BlockData blockData) {
        ItemMeta meta = bone.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Вибраний блок: " + blockData.getMaterial());
        lore.add(ChatColor.GRAY + "Розташування: " + blockData.getLocation().getBlockX() + ", " +
                blockData.getLocation().getBlockY() + ", " + blockData.getLocation().getBlockZ());
        meta.setLore(lore);
        bone.setItemMeta(meta);
    }

    // Обробка подій взаємодії гравця
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.BONE && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getDisplayName().equals(ChatColor.GOLD + "BlockTracker")) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    // Вибір блоку
                    Block block = event.getClickedBlock();
                    if (block != null) {
                        List<String[]> lookup = coreProtect.blockLookup(block, 86400); // Перевірка за останній день
                        if (!lookup.isEmpty()) {
                            String[] data = lookup.get(0);
                            String playerName = data[1]; // Останній гравець, який взаємодіяв з блоком
                            BlockData blockData = new BlockData(block.getType(), block.getLocation());
                            selectedBlocks.put(player.getUniqueId(), blockData);
                            updateBoneLore(item, blockData);
                            player.sendMessage(ChatColor.GREEN + "Вибрано блок: " + block.getType() + " at " +
                                    block.getLocation().getBlockX() + ", " + block.getLocation().getBlockY() + ", " +
                                    block.getLocation().getBlockZ());
                        }
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    // Підсвічування гравця
                    BlockData blockData = selectedBlocks.get(player.getUniqueId());
                    if (blockData != null) {
                        Player target = getTargetPlayer(player);
                        if (target != null) {
                            List<String[]> lookup = coreProtect.blockLookup((Block) blockData.getLocation(), 86400);
                            if (!lookup.isEmpty()) {
                                String[] data = lookup.get(0);
                                String playerName = data[1];
                                if (playerName.equals(target.getName())) {
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1)); // Підсвічування на 5 секунд
                                    player.sendMessage(ChatColor.GREEN + target.getName() + " is glowing now!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Отримання цільового гравця
    private Player getTargetPlayer(Player player) {
        return player.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distance(player.getLocation()) < 5)
                .findFirst()
                .orElse(null);
    }

    // Клас для зберігання даних про блок
    private static class BlockData {
        private final Material material;
        private final Location location;

        public BlockData(Material material, Location location) {
            this.material = material;
            this.location = location;
        }

        public Material getMaterial() {
            return material;
        }

        public Location getLocation() {
            return location;
        }
    }
}