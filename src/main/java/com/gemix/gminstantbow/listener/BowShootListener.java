package com.gemix.gminstantbow.listener;

import com.gemix.gminstantbow.Gemix;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BowShootListener implements Listener {

    private final Gemix plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public BowShootListener(Gemix plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        // Chỉ bắt sự kiện Right Click (Chuột phải)
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Lấy item ở tay chính
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        // Dùng API của MythicLib/MMOItems để kiểm tra
        NBTItem nbtItem = NBTItem.get(item);
        if (!nbtItem.hasType()) {
            return;
        }

        // Kiểm tra xem type có phải là BOW không
        String itemType = nbtItem.getType();
        if (itemType == null || !itemType.equalsIgnoreCase("BOW")) {
            return;
        }

        // Hủy sự kiện mặc định để không kéo cung
        event.setCancelled(true);

        // Xử lý Cooldown
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownMs = plugin.getConfig().getLong("cooldown-ms", 500);

        if (cooldowns.containsKey(uuid)) {
            long lastShot = cooldowns.get(uuid);
            if (currentTime - lastShot < cooldownMs) {
                // Đang trong thời gian cooldown

                return;
            }
        }
        
        // Cập nhật thời gian bắn
        cooldowns.put(uuid, currentTime);

        // Bắn mũi tên
        Arrow arrow = player.launchProjectile(Arrow.class);
        // Tăng tốc độ bay cho mũi tên (tùy chỉnh)
        arrow.setVelocity(player.getLocation().getDirection().multiply(3.0));

        // Tạo sự kiện EntityShootBowEvent ảo để MMOItems bắt và tính toán sát thương
        // Bắt buộc phải dùng để MMOItems biết mũi tên này là do cây cung nào bắn ra
        EntityShootBowEvent bowEvent = new EntityShootBowEvent(player, item, new ItemStack(Material.ARROW), arrow, EquipmentSlot.HAND, 1.0f, false);
        Bukkit.getPluginManager().callEvent(bowEvent);

        if (bowEvent.isCancelled()) {
            arrow.remove();
        }
    }
}
