package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Pestilence extends Ability {

    private final ArrayList<Entity> infected;

    public Pestilence(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        infected = new ArrayList<>();
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        new BukkitRunnable() {
            int drainer = 0;

            @Override
            public void run() {
                try {
                    caster.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation().add(0, 1.5, 0), 500, 250, 60, 250, 0);

                    if (pathway.getBeyonder().getSpirituality() <= 50) {
                        cancel();
                        return;
                    }

                    drainer++;
                    if (drainer >= 20) {
                        drainer = 0;
                        pathway.getSequence().removeSpirituality(50);
                    }

                    // Fetch nearby entities on the main thread
                    List<Entity> nearbyEntities = caster.getNearbyEntities(250, 60, 250);

                    for (Entity entity : nearbyEntities) {
                        if (infected.contains(entity))
                            continue;

                        if (!(entity instanceof LivingEntity livingEntity))
                            continue;

                        infected.add(entity);
                        applyPestilenceEffects(livingEntity);
                    }

                    // Cancel the task if ability is no longer active
                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1])
                        cancel();
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Pestilence");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    private void applyPestilenceEffects(LivingEntity entity) {
        new BukkitRunnable() {
            long counter = 0;

            @Override
            public void run() {
                if (counter % 80 == 0) {
                    if (counter < 8 * 20)
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
                    else if (counter <= 18 * 20) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.OOZING, 200, 4));
                    } else {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.OOZING, 200, 4));
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 4));
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 2));
                    }
                }

                counter++;

                if (counter >= 9223372036854775800L)
                    infected.remove(entity);

                if (!pathway.getSequence().getUsesAbilities()[identifier - 1])
                    infected.remove(entity);

                if (!entity.isValid())
                    cancel();
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        executeAbility(player.getLocation(), player, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.BONE, "Епідемічна Катастрофа", "50", identifier);
    }
}
