package dev.ua.ikeepcalm.mystical.parents.abilitiies;


import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import jline.internal.Nullable;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;


@Getter
public abstract class MobAbility extends Ability {

    protected int frequency;

    public MobAbility(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
    }

    public MobAbility(int frequency) {
        super(0, null, 0, null);
        this.frequency = frequency;
    }

    public abstract void useAbility(Location startLoc, Location endLoc, double multiplier, Entity user, @Nullable Entity target);

}
