package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Wind extends Ability {

    public Wind(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().getDirection().normalize().multiply(.5);

        Random random = new Random();

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1] || pathway.getBeyonder().getSpirituality() <= 8) {
                        cancel();
                        return;
                    }

                    // Schedule velocity and particle effects on the main thread
                    Bukkit.getScheduler().runTask(LordOfTheMinecraft.instance, () -> {
                        for (Entity entity : caster.getNearbyEntities(7, 7, 7)) {
                            entity.setVelocity(dir);
                        }

                        for (int i = 0; i < 8; i++) {
                            Location tempLoc = caster.getLocation().add(0, 1.5, 0).add(random.nextInt(10) - 5, random.nextInt(6) - 3, random.nextInt(10) - 5);
                            caster.getWorld().spawnParticle(Particle.CLOUD, tempLoc, 0, dir.getX(), dir.getY(), dir.getZ(), .4);
                        }
                    });

                    counter++;

                    if (counter >= 20) {
                        counter = 0;
                        pathway.getSequence().removeSpirituality(8);
                    }
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Wind");
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        executeAbility(player.getLocation(), player, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.FEATHER, "Порив Вітру", "8", identifier);
    }
}
