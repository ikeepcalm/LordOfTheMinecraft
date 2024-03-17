package dev.ua.ikeepcalm.entities.custom;

import dev.ua.ikeepcalm.utils.BeyonderMobUtil;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.MobAbility;
import jline.internal.Nullable;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public record CustomEntity(@NonNull String name, @NonNull String id, int rarity, @NonNull ItemStack drop,
                           @NonNull EntityType entityType, @Nullable Integer maxHealth,
                           @Nullable BeyonderMobUtil beyonderMobUtil, @Nullable EntityType spawnType, String particle,
                           boolean repeatingParticles, MobAbility[] abilities) {
}


