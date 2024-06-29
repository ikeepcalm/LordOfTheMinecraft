package dev.ua.ikeepcalm.mystical.parents.abilities;


import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.Nullable;


@Getter
public abstract class MobAbility extends Ability {

    protected int frequency;

    public MobAbility(int frequency) {
        super(0, null, 0, null);
        this.frequency = frequency;
    }

    public abstract void useAbility(Location startLoc, Location endLoc, double multiplier, Entity user, @Nullable Entity target);

}
