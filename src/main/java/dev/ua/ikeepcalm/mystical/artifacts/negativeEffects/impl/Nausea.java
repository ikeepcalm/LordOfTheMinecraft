package dev.ua.ikeepcalm.mystical.artifacts.negativeEffects.impl;

import dev.ua.ikeepcalm.mystical.artifacts.negativeEffects.NegativeEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Nausea extends NegativeEffect {

    public Nausea(int sequence, int delay, boolean constant) {
        super(sequence, delay, constant);
    }

    @Override
    public void doEffect(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 2, false, false));
    }
}
