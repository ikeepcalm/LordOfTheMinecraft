package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ColdWind extends Ability {

    public ColdWind(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().getDirection().normalize().multiply(1.25);

        Random random = new Random();

        new BukkitRunnable() {
            int counter = 0;

            int npcTimer = 20 * 5;

            @Override
            public void run() {
                try {
                    npcTimer--;

                    for (Entity entity : caster.getNearbyEntities(9, 9, 9)) {
                        entity.setVelocity(dir);
                        entity.setFreezeTicks(20 * 8);
                    }

                    for (int i = 0; i < 30; i++) {
                        Location tempLoc = caster.getLocation().add(0, 1, 0).add(random.nextInt(16) - 8, random.nextInt(10) - 5, random.nextInt(16) - 8);
                        caster.getWorld().spawnParticle(Particle.SNOWFLAKE, tempLoc, 0, dir.getX(), dir.getY(), dir.getZ(), .4);
                    }

                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1] || pathway.getBeyonder().getSpirituality() <= 10) {
                        cancel();
                        return;
                    }

                    counter++;

                    if (counter >= 20) {
                        counter = 0;
                        pathway.getSequence().removeSpirituality(10);
                    }

                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Cold Wind");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        executeAbility(player.getLocation(), player, 1);
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.FEATHER, "Холодний Вітер", "10/с", identifier);
    }
}
