package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Conceptualization extends Ability {

    public Conceptualization(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        boolean couldFly = player.getAllowFlight();
        float flySpeed = player.getFlySpeed();

        player.setFlySpeed(Math.min(flySpeed * 2, 1));

        new BukkitRunnable() {
            int counter = 20;

            int spiritCounter = 1;

            @Override
            public void run() {
                try {
                    GeneralPurposeUtil.drawParticlesForNearbyPlayers(Particle.ENCHANT, player.getEyeLocation(), 50, 1.1, 1.1, 1.1, 0);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.hidePlayer(LordOfTheMinecraft.instance, Conceptualization.this.player);
                    }

                    player.setAllowFlight(true);
                    player.setFlying(true);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 1, false, false, false));
                    player.setFireTicks(0);

                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1] || pathway.getBeyonder().getSpirituality() <= 420) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showPlayer(LordOfTheMinecraft.instance, Conceptualization.this.player);
                        }
                        player.setAllowFlight(couldFly);
                        player.setFlySpeed(flySpeed);
                        player.setFireTicks(0);
                        cancel();
                        return;
                    }

                    counter--;

                    if (counter <= 0) {
                        counter = 20;
                        pathway.getSequence().removeSpirituality(250 * spiritCounter);
                        spiritCounter++;
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Conceptualization");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.NETHER_STAR, "Астральні Руни", "200/c", identifier);
    }
}
