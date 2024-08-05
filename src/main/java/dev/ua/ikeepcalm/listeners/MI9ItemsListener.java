package dev.ua.ikeepcalm.listeners;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class MI9ItemsListener implements Listener {

    private final HashMap<UUID, Boolean> isInspecting = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.isEmpty() || item.getType().isAir())
            return;
        if (NBT.get(item, (nbt) -> {
            return nbt.getBoolean("mi9Monocle");
        })) {
            if (player.hasPermission(new Permission("lordoftheminecraft.mi9"))) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (isInspecting.containsKey(player.getUniqueId())) {
                        isInspecting.put(player.getUniqueId(), !isInspecting.get(player.getUniqueId()));
                        player.sendMessage("§cРежим дослідження злочинів дезактивовано!");
                    } else {
                        isInspecting.put(player.getUniqueId(), true);
                        player.sendMessage("§bРежим дослідження злочинів активовано!");
                    }
                    player.performCommand("co inspect");
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    player.sendMessage("§bРезультати дослідження поруч з вами:");
                    player.performCommand("co near");
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player victim = (Player) event.getEntity();
            if (event.getDamager().getType() == EntityType.PLAYER) {
                Player damager = (Player) event.getDamager();
                if (damager.hasPermission(new Permission("lordoftheminecraft.mi9"))) {
                    ItemStack item = damager.getItemInHand();
                    if (item.getType().isAir())
                        return;

                    if (NBT.get(item, (nbt) -> {
                        return nbt.getBoolean("mi9Stick");
                    })) {
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 2));
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 2));
                    }
                }
            }
        }
    }

}
