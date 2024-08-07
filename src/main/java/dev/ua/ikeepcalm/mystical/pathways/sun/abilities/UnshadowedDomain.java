package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Objects;

public class UnshadowedDomain extends Ability {
    public UnshadowedDomain(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        player = pathway.getBeyonder().getPlayer();

        Location loc = player.getLocation();
        ArrayList<Block> blocks = new ArrayList<>();

        int radius = 32;
        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Using async task for computationally heavy operations
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (int i = 15; i > -15; i--) {
                        for (int x = -radius; x <= radius; x++) {
                            for (int z = -radius; z <= radius; z++) {
                                if ((x * x) + (z * z) <= Math.pow(radius, 2)) {
                                    Block block = player.getWorld().getBlockAt((int) loc.getX() + x, (int) loc.getY() + i, (int) loc.getZ() + z);
                                    if (block.getType() == Material.AIR && block.getLocation().clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                            block.setType(Material.LIGHT);
                                            blocks.add(block);
                                        });
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Unshadowed Domain");
                    cancel();
                }
            }
        }.runTaskAsynchronously(LordOfTheMinecraft.instance);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    counter++;

                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                        Particle.DustOptions dustSphere = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DUST, loc, 65, 40, 40, 40, 0, dustSphere);
                        loc.getWorld().spawnParticle(Particle.END_ROD, loc, 65, 40, 40, 40, 0);

                        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 30, 30, 30)) {
                            if (entity instanceof LivingEntity) {
                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5, 1));
                            }
                        }
                    });

                    if (counter > 20 * 20) {
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            for (Block b : blocks) {
                                b.setType(Material.AIR);
                            }
                            pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                        });
                        cancel();
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Unshadowed Domain");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);

    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.SHROOMLIGHT, "Безтіння", "1000", identifier);
    }
}
