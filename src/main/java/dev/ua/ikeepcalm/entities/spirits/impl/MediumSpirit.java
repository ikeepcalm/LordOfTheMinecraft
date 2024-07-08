package dev.ua.ikeepcalm.entities.spirits.impl;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.spirits.Spirit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MediumSpirit extends Spirit implements Listener {

    public MediumSpirit(LivingEntity entity, double health, float particleOffset, int spawnRate, EntityType entityType, boolean visible, int spawnCount, @Nullable ItemStack drop, boolean undead, String name) {
        super(entity, health, particleOffset, spawnRate, entityType, visible, spawnCount, drop, undead, name);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public Spirit initNew(LivingEntity entity) {
        return new MediumSpirit(entity, health, particleOffset, spawnRate, entityType, visible, spawnCount, drop, undead, name);
    }

    private Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 6);

    @Override
    public void start() {
        dust = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 6);
    }

    @Override
    public void tick() {
        for (Entity nearby : entity.getNearbyEntities(35, 35, 35)) {
            if (!(nearby instanceof Player p))
                continue;

            p.spawnParticle(Particle.DUST, entity.getLocation(), 60, particleOffset, particleOffset, particleOffset, dust);
        }
    }

    @EventHandler
    public void onAttack(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() != entity)
            return;

        Fireball fireball = (Fireball) e.getEntity();
        fireball.setDirection(fireball.getDirection().multiply(5));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fireball.isValid())
                    cancel();

                fireball.getWorld().spawnParticle(Particle.DUST, fireball.getLocation(), 10, .2, .2, .2, dust);
                fireball.setVisualFire(false);
                fireball.setFireTicks(0);
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (entity == null) {
            return;
        }

        if (e.getEntity().getShooter() != entity)
            return;

        e.setCancelled(true);

        if (e.getHitEntity() == null)
            return;

        Entity hitEntity = e.getHitEntity();
        if (hitEntity instanceof LivingEntity livingEntity)
            livingEntity.damage(13, entity);

        hitEntity.teleport(entity);

    }
}
