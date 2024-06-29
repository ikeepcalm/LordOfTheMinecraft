package dev.ua.ikeepcalm.entities.spirits;

import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Spirit {

    @Getter
    protected final int spawnRate;
    @Getter
    protected final EntityType entityType;
    protected final boolean visible;
    @Getter
    protected final int spawnCount;
    @Getter
    protected final ItemStack drop;
    @Getter
    protected final boolean undead;
    @Getter
    protected final String name;

    protected final double health;
    protected final float particleOffset;
    @Getter
    protected final LivingEntity entity;

    public Spirit(LivingEntity entity, double health, float particleOffset, int spawnRate, EntityType entityType, boolean visible, int spawnCount, @Nullable ItemStack drop, boolean undead, String name) {
        this.entity = entity;
        this.health = health;
        this.particleOffset = particleOffset;
        this.drop = drop;

        this.spawnRate = spawnRate;
        this.entityType = entityType;
        this.visible = visible;
        this.spawnCount = spawnCount;
        this.undead = undead;
        this.name = name;
    }

    public abstract Spirit initNew(LivingEntity entity);

    public abstract void start();

    public abstract void tick();

    public void stop() {
    }

    public boolean isInvisible() {
        return !visible;
    }

}
