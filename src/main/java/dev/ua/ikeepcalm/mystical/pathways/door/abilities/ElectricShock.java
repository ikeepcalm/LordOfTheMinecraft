package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
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

        Random random = new Random();

        if (loc.getWorld() == null)
            return;

        Vector v = loc.getDirection().normalize().multiply(.5);
        outerloop:
        for (int i = 0; i < 30; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if ((!(entity instanceof Mob) && !(entity instanceof Player)) || entity == caster)
                    continue;
                ((LivingEntity) entity).damage(4 * multiplier, caster);
                break outerloop;
            }

            loc.add(v);
            loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 2, random.nextDouble(-.2, .2), random.nextDouble(-.2, .2), random.nextDouble(-.2, .2), 0);
        }
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        executeAbility(p.getEyeLocation(), p, 1);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.IRON_NUGGET, "Статичний Розряд", "40", identifier);
    }
}
