package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.NpcAbility;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlameControlling extends NpcAbility {

    private final boolean npc;

    public FlameControlling(int identifier, Pathway pathway, int sequence, Items items, boolean npc) {
        super(identifier, pathway, sequence, items);

        this.npc = npc;

        if (!npc)
            items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useNPCAbility(Location target, Entity caster, double multiplier) {
        if (!npc && pathway.getSequence().getCurrentSequence() == 7) {
            if (!p.getInventory().contains(Material.COAL) && !p.getInventory().contains(Material.CHARCOAL)) {
                Location noFuelLoc = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize());
                if (noFuelLoc.getWorld() == null)
                    return;
                noFuelLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, noFuelLoc, 25, 0.05, 0.05, 0.05, 0.15);
                return;
            }

            ItemStack item;
            for (int i = 0; i < p.getInventory().getContents().length; i++) {
                item = p.getInventory().getItem(i);
                if (item == null)
                    continue;
                if (item.getType() == Material.COAL || item.getType() == Material.CHARCOAL) {
                    item.setAmount(item.getAmount() - 1);
                    p.getInventory().setItem(i, item);
                    break;
                }
            }
        }


        Vector direction = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0).clone();

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                loc.add(direction);
                if (loc.getWorld() == null) {
                    cancel();
                    return;
                }
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.12, 0.12, 0.12, 0.025);
                loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc.clone().add(0, 0.12, 0), 6, 0.01, 0.01, 0.01, 0);

                if (!loc.getWorld().getNearbyEntities(loc, 5, 5, 5).isEmpty()) {
                    for (Entity entity : loc.getWorld().getNearbyEntities(loc, 5, 5, 5)) {
                        Vector v1 = new Vector(
                                loc.getX() + 0.25,
                                loc.getY() + 0.25,
                                loc.getZ() + 0.25
                        );
                        Vector v2 = new Vector(
                                loc.getX() - 0.25,
                                loc.getY() - 0.25,
                                loc.getZ() - 0.25
                        );
                        if (entity.getBoundingBox().overlaps(v1, v2) && entity instanceof Damageable && entity != caster && entity.getType() != EntityType.ARMOR_STAND) {
                            ((Damageable) entity).damage(8 * multiplier, caster);
                            entity.setFireTicks(250);
                            cancel();
                            return;
                        }
                    }
                }

                if (loc.getBlock().getType().isSolid() || counter >= 100) {
                    if (loc.getBlock().getType().isSolid() && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid())
                        loc.clone().add(0, 1, 0).getBlock().setType(Material.FIRE);
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useAbility() {
        double multiplier = getMultiplier();
        p = pathway.getBeyonder().getPlayer();

        useNPCAbility(p.getEyeLocation(), p, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.FIRE_CHARGE, "Підкорення Вогню", "45", identifier, sequence, pathway.getBeyonder().getPlayer().getName());
    }
}
