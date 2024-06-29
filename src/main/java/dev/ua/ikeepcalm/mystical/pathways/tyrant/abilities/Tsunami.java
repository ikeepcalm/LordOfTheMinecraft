package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.List;

public class Tsunami extends Ability implements Listener {

    public Tsunami(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        p = pathway.getBeyonder().getPlayer();
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        Vector dir = p.getLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        if (loc.getWorld() == null)
            return;

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            for (final int[] i = {0}; i[0] < 60; i[0]++) {
                final Location currentLoc = loc.clone();
                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 1, 1, 1)) {
                        if (entity.getType() == EntityType.ARMOR_STAND || entity == p)
                            continue;
                        i[0] = 60;
                        break;
                    }
                });

                if (i[0] >= 60 || loc.getBlock().getType().isSolid()) {
                    break;
                }

                loc.add(dir);
            }

            executeAbility(loc, p, getMultiplier());
        });
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.WATER_BUCKET, "Поклик Цунамі", "750", identifier);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (loc.getWorld() == null)
            return;

        Vector dir = caster.getLocation().getDirection().clone().normalize().setY(0).normalize();
        Location sideLoc = caster.getLocation();
        sideLoc.setPitch(0);
        sideLoc.setYaw(sideLoc.getYaw() + 90);
        Vector side = sideLoc.getDirection().normalize();

        Location startLoc = loc.clone().subtract(dir.clone().multiply(32.5)).subtract(side.clone().multiply(30)).subtract(0, 10, 0);

        List<Block> waterBlocksBefore = GeneralPurposeUtil.getWaterBlocksInSquare(loc.getBlock(), 45);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                for (int j = -10; j < Math.min(Math.sqrt(counter) * 1.5, 30); j++) {
                    Location tempLoc = startLoc.clone().add(0, j, 0);
                    for (int i = -30; i < 30; i++) {
                        tempLoc.add(side);
                        GeneralPurposeUtil.drawParticlesForNearbyPlayers(Particle.DRIPPING_WATER, tempLoc, 1, .5, .5, .5, 0);
                        Location waterLoc = tempLoc.clone();
                        if (!waterLoc.getBlock().getType().isSolid()) {
                            waterLoc.getBlock().setType(Material.WATER);
                        }

                        if (j == 0) {
                            for (Entity entity : loc.getWorld().getNearbyEntities(tempLoc, 1, 5, 1)) {
                                if (!GeneralPurposeUtil.testForValidEntity(entity, caster, true, true))
                                    continue;
                                LivingEntity livingEntity = (LivingEntity) entity;
                                livingEntity.damage(20 * multiplier, caster);
                            }
                        }
                    }
                }
                startLoc.add(dir);
                counter++;

                if (counter > 65) {
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<Block> waterBlocksAfter = GeneralPurposeUtil.getWaterBlocksInSquare(loc.getBlock(), 45).stream()
                                    .filter(block -> !waterBlocksBefore.contains(block))
                                    .toList();
                            for (Block b : waterBlocksAfter) {
                                b.setType(Material.AIR);
                            }
                        }
                    }.runTaskLater(LordOfTheMinecraft.instance, 20 * 3);
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

}
