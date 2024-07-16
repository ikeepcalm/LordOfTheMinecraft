package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Flash extends Ability {

    public Flash(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location loc = player.getEyeLocation();

        for (int i = 0; i < 20; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);

        if (loc.getWorld() == null)
            return;

        boolean placedLight = false;

        if (loc.getBlock().getType().getHardness() >= 0) {
            placedLight = true;
            loc.getBlock().setType(Material.LIGHT);
        }

        boolean finalPlacedLight = placedLight;
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                if (counter <= 17) {
                    loc.getWorld().spawnParticle(Particle.FLASH, loc, 0, 0, 0, 0, 0);
                }

                if (counter >= 20 * 8) {
                    if (finalPlacedLight && loc.getBlock().getType() == Material.LIGHT)
                        loc.getBlock().setType(Material.AIR);
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.TORCH, "Флешка", "25", identifier);
    }
}
