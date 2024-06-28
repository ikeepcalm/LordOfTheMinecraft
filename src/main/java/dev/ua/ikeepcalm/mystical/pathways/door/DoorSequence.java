package dev.ua.ikeepcalm.mystical.pathways.door;

import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Sequence;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DoorSequence extends Sequence {

    public DoorSequence(Pathway pathway, int optionalSequence) {
        super(pathway, optionalSequence);
        init();
    }

    @Override
    public List<Integer> getIds() {
        Integer[] ids = {2, 3, 8, 10, 11, 12, 14, 15, 16};
        return Arrays.asList(ids);
    }

    public void init() {
        usesAbilities = new boolean[30];
        Arrays.fill(usesAbilities, false);

        abilities = new ArrayList<>();

        sequenceEffects = new HashMap<>();
        sequenceResistances = new HashMap<>();

        initEffects();

        sequenceMultiplier = new HashMap<>();
        sequenceMultiplier.put(9, 1.0);
        sequenceMultiplier.put(8, 1.0);
        sequenceMultiplier.put(7, 1.0);
        sequenceMultiplier.put(6, 1.0);
        sequenceMultiplier.put(5, 1.5);
        sequenceMultiplier.put(4, 2.0);
        sequenceMultiplier.put(3, 2.25);
        sequenceMultiplier.put(2, 3.5);
        sequenceMultiplier.put(1, 5.0);
    }

    //Passive effects
    public void initEffects() {
        PotionEffect[] effects7 = {
                new PotionEffect(PotionEffectType.RESISTANCE, 60, 0, false, false, false),
                new PotionEffect(PotionEffectType.STRENGTH, 60, 0, false, false, false),
                new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, true),
        };
        sequenceEffects.put(7, effects7);

        PotionEffect[] effects6 = {
                new PotionEffect(PotionEffectType.RESISTANCE, 60, 1, false, false, false),
                new PotionEffect(PotionEffectType.STRENGTH, 60, 1, false, false, false),
                new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, true),
        };
        sequenceEffects.put(6, effects6);

        PotionEffect[] effects2 = {
                new PotionEffect(PotionEffectType.RESISTANCE, 60, 2, false, false, false),
                new PotionEffect(PotionEffectType.STRENGTH, 60, 2, false, false, false),
                new PotionEffect(PotionEffectType.SPEED, 60, 2, false, false, true),
                new PotionEffect(PotionEffectType.SATURATION, 60, 10, false, false, true),
        };
        sequenceEffects.put(2, effects2);
    }
}
