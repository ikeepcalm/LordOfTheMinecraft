package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SirenSong extends Ability {

    private Category selectedCategory = Category.CHAOTIC;
    private final Category[] categories = Category.values();
    private int selected = 0;

    public SirenSong(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        player = pathway.getBeyonder().getPlayer();
    }

    enum Category {
        CHAOTIC("Пісня Хаосу"),
        BOOST("Пісня Сили");

        private final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        switch (selectedCategory) {
            case CHAOTIC -> chaotic(player, getMultiplier());
            case BOOST -> boost(player);
        }
    }

    private void chaotic(Entity caster, double multiplier) {
        new BukkitRunnable() {
            int counter = 30 * 2;

            @Override
            public void run() {
                try {
                    counter--;
                    if (counter <= 0)
                        cancel();

                    GeneralPurposeUtil.drawParticlesForNearbyPlayers(Particle.NOTE, caster.getLocation(), 100, 10, 10, 10, 0);
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Siren Song");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 10);

        new BukkitRunnable() {
            int counter = 30 / 2;

            @Override
            public void run() {
                try {
                    counter--;
                    if (counter <= 0)
                        cancel();

                    GeneralPurposeUtil.damageNearbyEntities(caster, caster.getLocation(), 10, 10, 10, 1.5 * multiplier);
                    if (counter % 2 == 0) {
                        GeneralPurposeUtil.effectForNearbyEntities(caster, caster.getLocation(), 20, 20, 20, new PotionEffect(PotionEffectType.OOZING, 20 * 10, 1));
                        GeneralPurposeUtil.effectForNearbyEntities(caster, caster.getLocation(), 20, 20, 20, new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 1));
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Siren Song");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 40);
    }

    private void boost(Entity caster) {

        new BukkitRunnable() {
            int counter = 30 * 2;

            @Override
            public void run() {

                try {

                    counter--;
                    if (counter <= 0)
                        cancel();

                    GeneralPurposeUtil.drawParticlesForNearbyPlayers(Particle.NOTE, caster.getLocation(), 100, 10, 10, 10, 0);
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Siren Song");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 10);

        new BukkitRunnable() {
            int counter = 30 / 2;

            @Override
            public void run() {
                try {
                    counter--;
                    if (counter <= 0)
                        cancel();

                    if (caster instanceof LivingEntity livingEntity) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 8, 2, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 8, 2, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 8, 2, false, false));
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Siren Song");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 40);
    }

    @Override
    public ItemStack getItem() {
        player = pathway.getBeyonder().getPlayer();
        return TyrantItems.createItem(Material.MUSIC_DISC_MALL, "Спів Сирени", "400", identifier);
    }

    @Override
    //Cycle through categories on left click
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
        player = pathway.getBeyonder().getPlayer();
    }

    @Override
    //Display selected category
    public void onHold() {
        if (player != null) {
            player.sendActionBar(Component.text("Обрана пісня: ").color(NamedTextColor.BLUE).append(Component.text(selectedCategory.name).color(NamedTextColor.GOLD)));
        }
    }
}
