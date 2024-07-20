package dev.ua.ikeepcalm.mystical.pathways.demoness;

import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Sequence;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DemonessSequence extends Sequence implements Listener {

    public DemonessSequence(Pathway pathway, int optionalSequence) {
        super(pathway, optionalSequence);
        init();
    }

    @Override
    public List<Integer> getIds() {
        Integer[] ids = {2, 5, 8, 10};
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

    //Remove fall damage
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() != getPathway().getBeyonder().getPlayer() || e.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        e.setCancelled(true);
    }


    //Passive effects
    public void initEffects() {
        PotionEffect[] effects9 = {
                new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false, true),
                new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 0, false, false, true),
        };
        sequenceEffects.put(9, effects9);

        PotionEffect[] effects3 = {
                new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, true),
                new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 0, false, false, true),
                new PotionEffect(PotionEffectType.NIGHT_VISION, 620, 0, false, false, true)
        };
        sequenceEffects.put(3, effects3);

        PotionEffect[] effects2 = {
                new PotionEffect(PotionEffectType.STRENGTH, 60, 2, false, false, false),
                new PotionEffect(PotionEffectType.SPEED, 60, 2, false, false, true),
                new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 1, false, false, true),
                new PotionEffect(PotionEffectType.NIGHT_VISION, 620, 1, false, false, true),
                new PotionEffect(PotionEffectType.RESISTANCE, 60, 2, false, false, false),
        };
        sequenceEffects.put(2, effects2);
    }
}
