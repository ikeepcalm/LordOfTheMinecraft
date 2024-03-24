package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
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
        p = pathway.getBeyonder().getPlayer();

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        for (int i = 0; i < 15; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);

        if (loc.getWorld() == null)
            return;

        loc.getWorld().spawnParticle(Particle.SPELL_WITCH, p.getEyeLocation().subtract(0, .5, 0), 25, .5, .5, .5, 0);
        p.teleport(loc);
        p.setFallDistance(0);
        loc.getWorld().spawnParticle(Particle.SPELL_WITCH, p.getEyeLocation().subtract(0, .5, 0), 25, .5, .5, .5, 0);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.ENDER_PEARL, "Теле-стрибок", "20", identifier);
    }
}
