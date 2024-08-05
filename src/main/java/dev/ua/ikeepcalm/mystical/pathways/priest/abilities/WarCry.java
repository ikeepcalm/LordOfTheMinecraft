package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WarCry extends Ability {

    private static final int RADIUS = 30;
    private static final int COOLDOWN_SECONDS = 30;

    private boolean onCooldown = false;

    public WarCry(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        if (onCooldown) {
            pathway.getSequence().addSpirituality(100);
            player.sendMessage(Component.text("§cВи не можете використовувати цю здібність, оскільки вона на кулдауні!", NamedTextColor.RED));
            return;
        }

        for (Player itePlayer : Bukkit.getOnlinePlayers()) {
            if (itePlayer.getLocation().distanceSquared(itePlayer.getLocation()) <= RADIUS * RADIUS) {
                if (isAlly(itePlayer)) {
                    itePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1));
                    itePlayer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 0));
                    itePlayer.sendMessage(Component.text("Ваш командир використав Клич Війни, в атаку!", NamedTextColor.GREEN));
                } else {
                    itePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 1));
                    itePlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0));
                    itePlayer.sendMessage(Component.text("Ви на полі бою проти команди, під командуванням Червоного Жреця! Відступайте!", NamedTextColor.RED));
                }
            }
        }

        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 4.5f, 2f);

        startCooldown();
    }

    private void startCooldown() {
        onCooldown = true;
        Bukkit.getScheduler().runTaskLater(LordOfTheMinecraft.instance, () -> onCooldown = false, COOLDOWN_SECONDS * 20);
    }

    private boolean isAlly(Player player) {
        return pathway.getSequence().getSubordinates().contains(player.getUniqueId()) || player.getUniqueId().equals(this.player.getUniqueId());
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.GOAT_HORN, "Клич Війни", "100", identifier);
    }
}
