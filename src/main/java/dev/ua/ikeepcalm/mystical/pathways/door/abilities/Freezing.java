package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Freezing extends Ability {

    public Freezing(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location targetLoc, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().add(0, 1.5, 0).getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);
        if (loc.getWorld() == null)
            return;

        LivingEntity target = null;

        outerloop:
        for (int i = 0; i < 25; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if ((!(entity instanceof Mob) && !(entity instanceof Player)) || entity == caster)
                    continue;
                target = (LivingEntity) entity;
                break outerloop;
            }

            loc.add(dir);
        }

        if (target == null) {
            player.sendMessage("§cСутність не знайдено!");
            return;
        }

        LivingEntity finalTarget = target;
        finalTarget.setFreezeTicks(200);
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                finalTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5, 5, false, false, false));

                loc.getWorld().spawnParticle(Particle.SNOWFLAKE, finalTarget.getLocation().add(0, 1, 0), 10, .25, .25, .25, 0);

                counter++;
                if (counter >= 20 * 5) {
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        executeAbility(player.getLocation(), player, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.SNOWBALL, "Льодяний Затиск", "40", identifier);
    }
}
