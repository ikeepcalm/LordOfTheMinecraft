package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class RagingBlows extends Ability {

    public RagingBlows(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        executeAbility(p.getLocation(), p, getMultiplier());
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        World world = caster.getWorld();

        new BukkitRunnable() {
            int counter = 8;

            @Override
            public void run() {
                Random random = new Random();
                Location startLoc = MathVectorUtils.getRelativeLocation(caster, random.nextDouble(1, 2), random.nextDouble(-1.5, 1.5), random.nextDouble(-.5, .5));

                world.spawnParticle(Particle.EXPLOSION, startLoc, 10, 0, 0, 0, .25);
                world.spawnParticle(Particle.CRIT, startLoc, 10, 0, 0, 0, .25);
                world.playSound(startLoc, Sound.ENTITY_GENERIC_EXPLODE, .25f, 1f);

                for (Entity hit : world.getNearbyEntities(startLoc, 1.2, 1.2, 1.2)) {
                    if (hit instanceof LivingEntity livingEntity && hit.getType() != EntityType.ARMOR_STAND && hit != caster) {
                        livingEntity.damage(6.5 * multiplier, caster);
                    }
                }

                counter--;
                if (counter <= 0) {
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 20);
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.BONE_MEAL, "Кулаки Люті", "20", identifier);
    }
}
