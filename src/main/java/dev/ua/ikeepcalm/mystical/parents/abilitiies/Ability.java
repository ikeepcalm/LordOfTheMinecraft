package dev.ua.ikeepcalm.mystical.parents.abilitiies;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Ability {
    @Getter
    protected final int identifier;
    @Getter
    protected final Pathway pathway;
    protected Player p;
    @Getter
    protected final int sequence;
    protected final Items items;

    public Ability(int identifier, Pathway pathway, int sequence, Items items) {
        this.identifier = identifier;
        this.pathway = pathway;
        this.sequence = sequence;
        this.items = items;
    }


    public abstract void useAbility();

    public abstract ItemStack getItem();

    public void onHold() {
    }

    public void leftClick() {
    }

    public void removeAbility() {
    }

    public double getMultiplier() {
        double multiplier = 1;
        if (pathway.getSequence().getSequenceMultiplier().containsKey(pathway.getSequence().getCurrentSequence())) {
            multiplier = pathway.getSequence().getSequenceMultiplier().get(pathway.getSequence().getCurrentSequence());
        } else {
            for (int i = pathway.getSequence().getCurrentSequence(); i < 9; i++) {
                if (pathway.getSequence().getSequenceMultiplier().containsKey(i)) {
                    multiplier = pathway.getSequence().getSequenceMultiplier().get(i);
                }
            }
        }
        return multiplier;
    }

    public static double getMultiplier(Pathway pathway) {
        double multiplier = 1;
        if (pathway.getSequence().getSequenceMultiplier().containsKey(pathway.getSequence().getCurrentSequence())) {
            multiplier = pathway.getSequence().getSequenceMultiplier().get(pathway.getSequence().getCurrentSequence());
        } else {
            for (int i = pathway.getSequence().getCurrentSequence(); i < 9; i++) {
                if (pathway.getSequence().getSequenceMultiplier().containsKey(i)) {
                    multiplier = pathway.getSequence().getSequenceMultiplier().get(i);
                }
            }
        }
        return multiplier;
    }
}
