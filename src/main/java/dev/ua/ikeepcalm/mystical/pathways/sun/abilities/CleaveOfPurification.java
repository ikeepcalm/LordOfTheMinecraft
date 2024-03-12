package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

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
import org.bukkit.util.Vector;

import java.util.Objects;

public class CleaveOfPurification extends NpcAbility {
    public CleaveOfPurification(int identifier, Pathway pathway, int sequence, Items items, boolean npc) {
        super(identifier, pathway, sequence, items);
        if (!npc)
            items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useNPCAbility(Location target, Entity caster, double multiplier) {
        Location loc = caster.getLocation().add(0, 1, 0);
        Vector vector = loc.getDirection();

        for (int i = 0; i < 5; i++) {
            loc.add(vector);

            //Spawn Particles
            if (i == 2) {
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.5f);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc, 10, 0.15, 0.15, 0.15, 0);
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 80, 0.2, 0.2, 0.2, dust);
            }

            if (i < 2) {
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 2f);
                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.END_ROD, loc, 10, 0.25, 0.25, 0.25, 0);
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 80, 0.3, 0.3, 0.3, dust);
            }

            if (loc.getWorld().getNearbyEntities(loc, 1, 1, 1).isEmpty())
                continue;

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if (entity == caster)
                    continue;
                Location entLoc = entity.getLocation();
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.getCategory() == EntityCategory.UNDEAD) {
                        ((Damageable) entity).damage(28 * multiplier, caster);
                    } else {
                        ((Damageable) entity).damage(12 * multiplier, caster);
                    }
                    Objects.requireNonNull(entLoc.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, entLoc, 200, 0.2, 0.2, 0.2, 0.15);
                }
            }
        }
    }

    @Override
    public void useAbility() {
        double multiplier = getMultiplier();

        p = pathway.getBeyonder().getPlayer();
        Location loc = p.getLocation().add(0, 1, 0);
        Vector vector = loc.getDirection();

        for (int i = 0; i < 5; i++) {
            loc.add(vector);

            //Spawn Particles
            if (i == 2) {
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.5f);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc, 10, 0.15, 0.15, 0.15, 0);
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 80, 0.2, 0.2, 0.2, dust);
            }

            if (i < 2) {
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 2f);
                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.END_ROD, loc, 10, 0.25, 0.25, 0.25, 0);
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 80, 0.3, 0.3, 0.3, dust);
            }

            if (loc.getWorld().getNearbyEntities(loc, 1, 1, 1).isEmpty())
                continue;

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if (entity.getUniqueId() == pathway.getUuid())
                    continue;
                Location entLoc = entity.getLocation();
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.getCategory() == EntityCategory.UNDEAD) {
                        ((Damageable) entity).damage(28 * multiplier, p);
                    } else {
                        if (entity != p)
                            ((Damageable) entity).damage(12 * multiplier, p);
                    }
                    Objects.requireNonNull(entLoc.getWorld()).spawnParticle(Particle.FIREWORKS_SPARK, entLoc, 200, 0.2, 0.2, 0.2, 0.15);
                }
            }
        }
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.HONEYCOMB, "Екзорцизм", "50", identifier, 7, Objects.requireNonNull(Bukkit.getPlayer(pathway.getUuid())).getName());
    }
}