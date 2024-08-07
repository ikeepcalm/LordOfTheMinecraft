package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DarkFlames extends Ability {

    public DarkFlames(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location target, Entity caster, double multiplier) {
        Vector vector;
        Location loc = caster.getLocation().add(0, 1.5, 0).clone();

        vector = caster.getLocation().getDirection().normalize();

        if (loc.getWorld() == null)
            return;

        World world = loc.getWorld();

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                if (counter >= 50) {
                    cancel();
                    return;
                }

                loc.add(vector);
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 40, .25, .25, .25, 0);

                if (loc.getBlock().getType().isSolid()) {
                    loc.clone().subtract(vector).getBlock().setType(Material.SOUL_FIRE);
                    cancel();
                    return;
                }

                boolean cancelled = false;
                for (Entity entity : world.getNearbyEntities(loc, 1, 1, 1)) {
                    if ((!(entity instanceof Mob) && !(entity instanceof Player)) || entity == caster || entity.getType() == EntityType.ARMOR_STAND)
                        continue;

                    ((LivingEntity) entity).damage(15 * multiplier, caster);
                    entity.setFireTicks(20 * 20);
                    cancelled = true;
                }

                if (cancelled)
                    cancel();
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        executeAbility(player.getLocation(), player, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.SOUL_CAMPFIRE, "Темний Вогонь", "35", identifier);
    }
}
