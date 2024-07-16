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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Blink extends Ability {

    public Blink(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location loc = player.getEyeLocation().clone();  // Clone to avoid modifying original location

        // Asynchronous task to compute target location
        Bukkit.getScheduler().runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            for (int i = 0; i < 15; i++) {
                if (loc.getBlock().getType().isSolid())
                    break;
                loc.add(dir);
            }

            loc.subtract(dir);

            if (loc.getWorld() == null)
                return;

            // Synchronous task to perform teleportation and particle effects
            Bukkit.getScheduler().runTask(LordOfTheMinecraft.instance, () -> {
                loc.getWorld().spawnParticle(Particle.WITCH, player.getEyeLocation().subtract(0, .5, 0), 25, .5, .5, .5, 0);
                player.teleport(loc);
                player.setFallDistance(0);
                loc.getWorld().spawnParticle(Particle.WITCH, player.getEyeLocation().subtract(0, .5, 0), 25, .5, .5, .5, 0);
            });
        });
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.ENDER_PEARL, "Теле-стрибок", "80", identifier);
    }
}
