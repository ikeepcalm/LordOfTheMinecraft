package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.NpcAbility;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
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

public class Pestilence extends NpcAbility {

    private final ArrayList<Entity> infected;

    private final boolean npc;

    public Pestilence(int identifier, Pathway pathway, int sequence, Items items, boolean npc) {
        super(identifier, pathway, sequence, items);

        this.npc = npc;

        if (!npc)
            items.addToSequenceItems(identifier - 1, sequence);
        infected = new ArrayList<>();
    }

    @Override
    public void useNPCAbility(Location loc, Entity caster, double multiplier) {
        new BukkitRunnable() {
            int drainer = 0;
            int npcCounter = 20 * 20;

            @Override
            public void run() {
                caster.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation().add(0, 1.5, 0), 500, 250, 60, 250, 0);

                if (npc)
                    npcCounter--;

                if (npc && npcCounter <= 0)
                    cancel();

                if (!npc && pathway.getBeyonder().getSpirituality() <= 50) {
                    cancel();
                    return;
                }

                if (!npc) {
                    drainer++;
                    if (drainer >= 20) {
                        drainer = 0;
                        pathway.getSequence().removeSpirituality(50);
                    }
                }

                for (Entity entity : caster.getNearbyEntities(250, 60, 250)) {
                    if (infected.contains(entity))
                        continue;

                    if (!(entity instanceof LivingEntity livingEntity))
                        continue;

                    infected.add(entity);
                    new BukkitRunnable() {
                        long counter = 80;

                        @Override
                        public void run() {

                            if (npc && npcCounter <= 0)
                                cancel();

                            if (counter % 80 == 0) {
                                if (counter < 8 * 20)
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
                                else if (counter <= 18 * 20) {
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 4));
                                } else {
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 4));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 4));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 2));
                                }
                            }

                            counter++;

                            if (counter >= 9223372036854775800L)
                                infected.remove(entity);

                            if (!npc && !pathway.getSequence().getUsesAbilities()[identifier - 1])
                                infected.remove(entity);

                            if (!caster.getNearbyEntities(250, 60, 250).contains(entity)) {
                                infected.remove(entity);
                            }

                            if (!infected.contains(entity)) {
                                cancel();
                                return;
                            }

                            if (!livingEntity.isValid())
                                cancel();

                        }
                    }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
                }

                if (!npc && !pathway.getSequence().getUsesAbilities()[identifier - 1])
                    cancel();
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        useNPCAbility(p.getLocation(), p, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.BONE, "Pestilence", "50/s", identifier);
    }
}
