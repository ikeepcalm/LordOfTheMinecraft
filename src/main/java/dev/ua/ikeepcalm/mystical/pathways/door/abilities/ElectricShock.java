package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class ElectricShock extends Ability {

    public ElectricShock(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location target, Entity caster, double multiplier) {
        Location loc = caster.getLocation().add(0, 1.5, 0);

        if (loc.getWorld() == null)
            return;

        // Asynchronous task to perform the main logic
        Bukkit.getScheduler().runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            Random random = new Random();
            Vector v = loc.getDirection().normalize().multiply(.5);

            for (int i = 0; i < 30; i++) {
                Location finalLoc = loc.clone();
                // Run Spigot API interactions on the main thread
                Bukkit.getScheduler().runTask(LordOfTheMinecraft.instance, () -> {
                    for (Entity entity : finalLoc.getWorld().getNearbyEntities(finalLoc, 1, 1, 1)) {
                        if ((!(entity instanceof Mob) && !(entity instanceof Player)) || entity == caster)
                            continue;
                        ((LivingEntity) entity).damage(4 * multiplier, caster);
                        return;
                    }
                    finalLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, finalLoc, 2, random.nextDouble(-.2, .2), random.nextDouble(-.2, .2), random.nextDouble(-.2, .2), 0);
                });
                loc.add(v);
            }
        });
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        executeAbility(player.getEyeLocation(), player, 1);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.IRON_NUGGET, "Статичний Розряд", "20", identifier);
    }
}
