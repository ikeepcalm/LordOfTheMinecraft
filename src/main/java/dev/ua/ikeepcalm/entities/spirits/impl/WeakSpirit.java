package dev.ua.ikeepcalm.entities.spirits.impl;

import dev.ua.ikeepcalm.entities.spirits.Spirit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeakSpirit extends Spirit {

    public WeakSpirit(LivingEntity entity, double health, float particleOffset, int spawnRate, EntityType entityType, boolean visible, int spawnCount, ItemStack drop, boolean undead, String name) {
        super(entity, health, particleOffset, spawnRate, entityType, visible, spawnCount, drop, undead, name);
    }

    @Override
    public Spirit initNew(LivingEntity entity) {
        return new WeakSpirit(entity, health, particleOffset, spawnRate, entityType, visible, spawnCount, drop, undead, name);
    }

    private Particle.DustOptions dust;

    @Override
    public void start() {
        dust = new Particle.DustOptions(Color.fromRGB(196, 21, 14), 2);
    }

    @Override
    public void tick() {
        for (Entity nearby : entity.getNearbyEntities(35, 35, 35)) {
            if (!(nearby instanceof Player p))
                continue;

            p.spawnParticle(Particle.DUST, entity.getLocation(), 20, particleOffset, particleOffset, particleOffset, dust);
        }
    }
}
