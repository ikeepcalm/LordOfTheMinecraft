package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ExtremeColdness extends Ability {

    private final HashMap<Entity, Boolean> inUse;

    public ExtremeColdness(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        p = pathway.getBeyonder().getPlayer();
        inUse = new HashMap<>();
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        executeAbility(p.getLocation(), p, getMultiplier());
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (inUse.get(caster) != null && inUse.get(caster)) {
            inUse.replace(caster, false);
            return;
        }

        if (inUse.get(caster) == null) {
            inUse.put(caster, true);
        } else {
            inUse.replace(caster, true);
        }

        UUID uuid = UUID.randomUUID();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        new BukkitRunnable() {
            @Override
            public void run() {
                CompletableFuture.runAsync(() -> {
                    for (Block block : GeneralPurposeUtil.getNearbyBlocksInSphere(caster.getLocation(), 20, false, true, false)) {
                        if (block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE) continue;

                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            if (block.getType().getHardness() <= .4f || block.getType() == Material.WATER) {
                                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                                block.setType(Material.ICE);
                            } else {
                                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                                block.setType(Material.PACKED_ICE);
                            }
                            GeneralPurposeUtil.drawDustsForNearbyPlayers(block.getLocation(), 1, 0, 0.5, 0, new Particle.DustOptions(Color.fromRGB(88, 200, 237), 1f));
                        });
                    }
                });

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    for (Entity entity : caster.getWorld().getNearbyEntities(caster.getLocation(), 60, 60, 60)) {
                        if (entity instanceof LivingEntity livingEntity && entity != caster) {
                            livingEntity.damage(8 * multiplier);
                            for (double x = -livingEntity.getWidth() + .5; x < livingEntity.getWidth() + .5; x++) {
                                for (double z = -livingEntity.getWidth() + .5; z < livingEntity.getWidth() + .5; z++) {
                                    for (int i = 0; i < livingEntity.getHeight(); i++) {
                                        livingEntity.getLocation().add(x, i, z).getBlock().setType(Material.PACKED_ICE);
                                    }
                                }
                            }
                        }
                    }

                    if (!caster.isValid() || inUse.get(caster) == null || !inUse.get(caster)) {
                        rollbackChanges(uuid);
                        cancel();
                    }
                });
            }

            @Override
            public void cancel() {
                scheduler.cancelTasks(LordOfTheMinecraft.instance);
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 8);
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.BLUE_ICE, "Крижаний Катаклізм", "2000", identifier);
    }
}
