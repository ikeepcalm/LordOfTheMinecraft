package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
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

public class HolyLightSummoning extends Ability {

    public HolyLightSummoning(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        loc.add(0, 14, 0);

        final Material[] lastMaterial = {loc.getBlock().getType()};
        BukkitScheduler scheduler = Bukkit.getScheduler();

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    counter++;

                    scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                        // Particles
                        spawnParticles(loc);

                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            loc.getBlock().setType(lastMaterial[0]);
                            loc.subtract(0, 1, 0);
                            lastMaterial[0] = loc.getBlock().getType();
                            loc.getBlock().setType(Material.LIGHT);

                            if ((lastMaterial[0].isSolid() && counter >= 12) || counter >= 200) {
                                loc.getBlock().setType(lastMaterial[0]);
                                counter = 0;
                                cancel();

                                // Light that stays at the ground for a bit
                                lightGround(loc);

                                // Damage nearby entities
                                damageNearbyEntities(loc, caster, multiplier);

                                // Particles on ground
                                spawnGroundParticles(loc);
                            }
                        });
                    });
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Holy Light Summoning");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    private void spawnParticles(Location loc) {
        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.END_ROD, loc.getX() + 3.2, loc.getY(), loc.getZ(), 4, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() - 3.2, loc.getY(), loc.getZ(), 4, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX(), loc.getY(), loc.getZ() + 3.2, 4, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX(), loc.getY(), loc.getZ() - 3.2, 4, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + 2.4, loc.getY(), loc.getZ() + 2.4, 3, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() - 2.4, loc.getY(), loc.getZ() - 2.4, 3, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() - 2.4, loc.getY(), loc.getZ() + 2.4, 3, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + 2.4, loc.getY(), loc.getZ() - 2.4, 3, 0.1, 0, 0.1, 0);

        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + 2.7, loc.getY(), loc.getZ(), 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() - 2.7, loc.getY(), loc.getZ(), 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ() + 2.7, 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ() - 2.7, 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + 1.9, loc.getY(), loc.getZ() + 1.9, 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() - 1.9, loc.getY(), loc.getZ() - 1.9, 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() - 1.9, loc.getY(), loc.getZ() + 1.9, 7, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + 1.9, loc.getY(), loc.getZ() - 1.9, 7, 0.2, 0, 0.2, 0);

        Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.25f);
        for (double i = 0; i < 3.2; i += 0.8) {
            for (int j = 0; j < 100; j++) {
                double x = i * Math.cos(j);
                double z = i * Math.sin(j);
                loc.getWorld().spawnParticle(Particle.DUST, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, dust);
                if (j % 2 == 0)
                    loc.getWorld().spawnParticle(Particle.FIREWORK, loc.getX() + x, loc.getY() + 1, loc.getZ() + z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void lightGround(Location loc) {
        Location lightLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
        Material[] lightMaterial = {
                lightLoc.getBlock().getType(),
                lightLoc.add(1, 0, 0).getBlock().getType(),
                lightLoc.add(-2, 0, 0).getBlock().getType(),
                lightLoc.add(1, 0, 1).getBlock().getType(),
                lightLoc.add(0, 0, -2).getBlock().getType()
        };

        Block[] lightBlock = {
                lightLoc.getBlock(),
                lightLoc.add(1, 0, 0).getBlock(),
                lightLoc.add(-2, 0, 0).getBlock(),
                lightLoc.add(1, 0, 1).getBlock(),
                lightLoc.add(0, 0, -2).getBlock()
        };

        for (Block b : lightBlock) {
            b.setType(Material.LIGHT);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < lightBlock.length; i++) {
                    Block b = lightBlock[i];
                    b.setType(lightMaterial[i]);
                }
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 2 * 20);
    }

    private void damageNearbyEntities(Location loc, Entity caster, double multiplier) {
        ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) loc.getWorld().getNearbyEntities(loc, 15, 15, 15);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                    ((Damageable) entity).damage(22 * multiplier, caster);
                } else {
                    if (entity != caster)
                        ((Damageable) entity).damage(12 * multiplier, caster);
                }
            }
        }
    }

    private void spawnGroundParticles(Location loc) {
        loc.add(0, 1, 0);
        Particle.DustOptions dustRipple = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.5f);
        new BukkitRunnable() {
            double radius = 1.8;
            int factor = 0;

            @Override
            public void run() {
                radius = radius + 0.75;
                for (int i = 0; i < 100; i++) {
                    factor++;
                    double x = radius * Math.cos(factor);
                    double z = radius * Math.sin(factor);
                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0, 0, 0, 0);
                    loc.getWorld().spawnParticle(Particle.FIREWORK, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, 0.1, 0, 0.1, 0.15);
                    loc.getWorld().spawnParticle(Particle.DUST, loc.getX() + x + 0.2, loc.getY(), loc.getZ() + z + 0.2, 3, dustRipple);
                }

                if (radius >= 9) {
                    cancel();
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        player = pathway.getBeyonder().getPlayer();

        double multiplier = getMultiplier();

        // Get block player is looking at
        BlockIterator iter = new BlockIterator(player, 22);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }
        Location loc = lastBlock.getLocation();

        executeAbility(loc, player, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.BLAZE_ROD, "Проміння", "50", identifier);
    }
}
