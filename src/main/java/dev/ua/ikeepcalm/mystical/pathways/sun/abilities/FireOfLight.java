package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class FireOfLight extends Ability {

    public FireOfLight(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location target, Entity caster, double multiplier) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    if (!target.getBlock().getType().isSolid()) {
                        target.getBlock().setType(Material.FIRE);
                    }

                    target.add(1, 0, 0);

                    if (!target.getBlock().getType().isSolid()) {
                        target.getBlock().setType(Material.FIRE);
                    }

                    target.add(-2, 0, 0);

                    if (!target.getBlock().getType().isSolid()) {
                        target.getBlock().setType(Material.FIRE);
                    }
                    target.add(1, 0, -1);
                    if (!target.getBlock().getType().isSolid()) {
                        target.getBlock().setType(Material.FIRE);
                    }
                    target.add(0, 0, 2);
                    if (!target.getBlock().getType().isSolid()) {
                        target.getBlock().setType(Material.FIRE);
                    }
                    target.subtract(0, 0, 1);

                    target.add(0.5, 0.5, 0.5);

                    final Material[] lightBlock = {target.getBlock().getType()};
                    target.getBlock().setType(Material.LIGHT);

                    new BukkitRunnable() {
                        int counter = 0;
                        final UUID uuid = UUID.randomUUID();

                        @Override
                        public void run() {
                            counter++;

                            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                Objects.requireNonNull(target.getWorld()).spawnParticle(Particle.FLAME, target, 50, 0.75, 0.75, 0.75, 0);
                                target.getWorld().spawnParticle(Particle.END_ROD, target, 8, 0.75, 0.75, 0.75, 0);

                                // damage nearby entities
                                ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) target.getWorld().getNearbyEntities(target, 2, 2, 2);
                                for (Entity entity : nearbyEntities) {
                                    if (entity instanceof LivingEntity livingEntity) {
                                        if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                                            ((Damageable) entity).damage(10 * multiplier, caster);
                                            livingEntity.setFireTicks(10 * 20);
                                        }
                                        if (entity != caster)
                                            livingEntity.setFireTicks(10 * 20);
                                    }
                                }
                            });

                            if (counter >= 5 * 20) {
                                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                    logBlockBreak(uuid, new CustomLocation(target));
                                    target.getBlock().setType(Material.AIR);
                                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                                    target.getBlock().setType(lightBlock[0]);
                                });
                                cancel();
                            }
                        }

                        @Override
                        public void cancel() {
                            super.cancel();
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> rollbackChanges(uuid));
                        }
                    }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Fire of Light");
                    cancel();
                }
            }
        }.runTask(LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        double multiplier = getMultiplier();

        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        // get block player is looking at
        BlockIterator iter = new BlockIterator(player, 15);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }

        // setting the fire
        Location loc = lastBlock.getLocation().add(0, 1, 0);

        executeAbility(loc, player, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.BLAZE_POWDER, "Вогонь", "20", identifier);
    }
}
