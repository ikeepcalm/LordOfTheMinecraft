package dev.ua.ikeepcalm.mystical.pathways.priest;

import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Sequence;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PriestSequence extends Sequence {

    public PriestSequence(Pathway pathway, int optionalSequence) {
        super(pathway, optionalSequence);
        init();
    }

    @Override
    public List<Integer> getIds() {
        Integer[] ids = {0};
        return Arrays.asList(ids);
    }

    public void init() {
        usesAbilities = new boolean[19];
        Arrays.fill(usesAbilities, false);

        abilities = new ArrayList<>();

        sequenceEffects = new HashMap<>();
        sequenceResistances = new HashMap<>();

        initEffects();

        sequenceMultiplier = new HashMap<>();
        sequenceMultiplier.put(5, 1.5);
        sequenceMultiplier.put(4, 2.0);
        sequenceMultiplier.put(3, 2.25);
        sequenceMultiplier.put(2, 3.5);
        sequenceMultiplier.put(1, 5.0);
    }

    //Passive effects
    public void initEffects() {
        PotionEffect[] effects9 = {
                new PotionEffect(PotionEffectType.REGENERATION, 5, 0, false, false, true),
        };
        sequenceEffects.put(9, effects9);

        PotionEffect[] effects7 = {
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 1, false, false, true)
        };
        sequenceEffects.put(6, effects7);

        PotionEffect[] effects4 = {
                new PotionEffect(PotionEffectType.RESISTANCE, 60, 2, false, false, false),
        };
        sequenceEffects.put(2, effects4);
    }
}
