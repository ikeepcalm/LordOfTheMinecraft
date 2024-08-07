package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class HolySong extends Ability {

    public HolySong(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Sound[] sounds = Sound.values();
        Sound randomSound = sounds[(int) (Math.random() * sounds.length)];
        scheduler.runTask(LordOfTheMinecraft.instance, () -> player.getWorld().playSound(player, randomSound, 10f, 1f));

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                        player.getWorld().spawnParticle(Particle.NOTE, player.getLocation(), 50, 5, 5, 5);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 0, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, 0, false, false, false));
                    });

                    counter++;
                    if (counter >= 95) {
                        counter = 0;
                        cancel();
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Holy Song");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 1, 20);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.MUSIC_DISC_PIGSTEP, "Балада", "30", identifier);
    }
}
