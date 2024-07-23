package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Objects;

public class CleaveOfPurification extends Ability {
    public CleaveOfPurification(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        double multiplier = getMultiplier();

        player = pathway.getBeyonder().getPlayer();
        Location loc = player.getLocation().add(0, 1, 0);
        Vector vector = loc.getDirection();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 5; i++) {
                        loc.add(vector);

                        int finalI = i;
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            // Spawn Particles
                            if (finalI == 2) {
                                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.5f);
                                loc.getWorld().spawnParticle(Particle.END_ROD, loc, 10, 0.15, 0.15, 0.15, 0);
                                loc.getWorld().spawnParticle(Particle.DUST, loc, 80, 0.2, 0.2, 0.2, dust);
                            }

                            if (finalI < 2) {
                                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 2f);
                                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.END_ROD, loc, 10, 0.25, 0.25, 0.25, 0);
                                loc.getWorld().spawnParticle(Particle.DUST, loc, 80, 0.3, 0.3, 0.3, dust);
                            }

                            if (loc.getWorld().getNearbyEntities(loc, 1, 1, 1).isEmpty()) return;

                            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                                if (entity.getUniqueId() == pathway.getUuid()) continue;
                                Location entLoc = entity.getLocation();
                                if (entity instanceof LivingEntity livingEntity) {
                                    if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                                        livingEntity.damage(28 * multiplier, player);
                                    } else {
                                        if (entity != player) livingEntity.damage(12 * multiplier, player);
                                    }
                                    Objects.requireNonNull(entLoc.getWorld()).spawnParticle(Particle.FIREWORK, entLoc, 200, 0.2, 0.2, 0.2, 0.15);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Cleave of Purification");
                    cancel();
                }
            }
        }.runTaskAsynchronously(LordOfTheMinecraft.instance);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.HONEYCOMB, "Екзорцизм", "50", identifier);
    }
}
