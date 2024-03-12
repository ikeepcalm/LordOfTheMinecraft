package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.NpcAbility;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class LightOfPurification extends NpcAbility {

    private final boolean npc;

    public LightOfPurification(int identifier, Pathway pathway, int sequence, Items items, boolean npc) {
        super(identifier, pathway, sequence, items);
        if (!npc)
            items.addToSequenceItems(identifier - 1, sequence);

        this.npc = npc;
    }

    @Override
    public void useNPCAbility(Location ignored, Entity caster, double multiplier) {
        Location loc = caster.getLocation();

        //Spawning Particles
        loc.add(0, 1, 0);
        new BukkitRunnable() {
            double radius = 1.8;

            @Override
            public void run() {
                Particle.DustOptions dustRipple = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                radius = radius + 0.75;
                for (int j = 0; j < 30 * radius; j++) {
                    double x = radius * Math.cos(j);
                    double z = radius * Math.sin(j);
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc.getX() + x, loc.getY(), loc.getZ() + z, 5, 0.2, 1, 0.2, 0, dustRipple);
                    Random rand = new Random();
                    if (j % (rand.nextInt(8) + 1) == 0)
                        loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0.2, 1, 0.2, 0);

                    //checking for entities
                    for (Entity entity : loc.getWorld().getNearbyEntities(new Location(loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z), 1, 3, 1)) {
                        if (entity instanceof LivingEntity) {
                            if (((LivingEntity) entity).getCategory() == EntityCategory.UNDEAD)
                                ((Damageable) entity).damage(25 * multiplier, caster);
                        }
                    }
                }

                if (radius >= 20) {
                    cancel();
                    if (!npc)
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        p = pathway.getBeyonder().getPlayer();

        double multiplier = getMultiplier();

        useNPCAbility(p.getLocation(), p, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.GLOWSTONE, "Кремація", "120", identifier, 5, Objects.requireNonNull(Bukkit.getPlayer(pathway.getUuid())).getName());
    }
}
