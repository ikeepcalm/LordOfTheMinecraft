package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;
import java.util.Random;

public class LightOfPurification extends Ability {

    public LightOfPurification(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location ignored, Entity caster, double multiplier) {
        Location loc = caster.getLocation();

        // Spawning Particles
        loc.add(0, 1, 0);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            double radius = 1.8;

            @Override
            public void run() {
                try {

                    scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                        Particle.DustOptions dustRipple = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                        radius = radius + 0.75;
                        for (int j = 0; j < 30 * radius; j++) {
                            double x = radius * Math.cos(j);
                            double z = radius * Math.sin(j);
                            int finalJ = j;
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DUST, loc.getX() + x, loc.getY(), loc.getZ() + z, 5, 0.2, 1, 0.2, 0, dustRipple);
                                Random rand = new Random();
                                if (finalJ % (rand.nextInt(8) + 1) == 0)
                                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0.2, 1, 0.2, 0);

                                // Checking for entities
                                for (Entity entity : loc.getWorld().getNearbyEntities(new Location(loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z), 1, 3, 1)) {
                                    if (entity instanceof LivingEntity) {
                                        if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                                            ((Damageable) entity).damage(25 * multiplier, caster);
                                        }
                                    }
                                }
                            });
                        }

                        if (radius >= 20) {
                            cancel();
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> pathway.getSequence().getUsesAbilities()[identifier - 1] = false);
                        }
                    });
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Light of Purification");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        player = pathway.getBeyonder().getPlayer();

        double multiplier = getMultiplier();

        executeAbility(player.getLocation(), player, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.GLOWSTONE, "Кремація", "120", identifier);
    }
}
