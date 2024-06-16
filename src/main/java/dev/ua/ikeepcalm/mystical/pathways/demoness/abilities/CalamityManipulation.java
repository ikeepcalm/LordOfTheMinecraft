package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.entities.disasters.Blizzard;
import dev.ua.ikeepcalm.entities.disasters.Earthquake;
import dev.ua.ikeepcalm.entities.disasters.Tornado;
import dev.ua.ikeepcalm.entities.disasters.Tsunami;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class CalamityManipulation extends Ability {

    private Category selectedCategory = Category.BLIZZARD;
    private final Category[] categories = Category.values();
    private int selected = 0;

    public CalamityManipulation(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (!(caster instanceof LivingEntity livingEntity))
            return;
        switch ((new Random().nextInt(3))) {
            case 0 -> (new Blizzard(livingEntity)).spawnDisaster(livingEntity, loc);
            case 1 -> (new Earthquake(livingEntity)).spawnDisaster(livingEntity, caster.getLocation());
            case 2 -> (new Tornado(livingEntity)).spawnDisaster(livingEntity, loc);
        }
    }

    enum Category {
        TORNADO("§fСмерч"),
        BLIZZARD("§bЗавірюха"),
        TSUNAMI("§9Цунамі"),
        EARTHQUAKE("§2Землетрус");

        private final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        for (int i = 0; i < 200; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);

        switch (selectedCategory) {
            case TORNADO -> new Tornado(p).spawnDisaster(p, loc);
            case BLIZZARD -> new Blizzard(p).spawnDisaster(p, loc);
            case TSUNAMI -> new Tsunami(p).spawnDisaster(p, loc);
            case EARTHQUAKE -> new Earthquake(p).spawnDisaster(p, loc);
        }
    }

    @Override
    //Cycle through categories on left click
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
    }

    @Override
    //Display selected category
    public void onHold() {
        if (p == null)
            p = pathway.getBeyonder().getPlayer();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обраний катаклізм: §f" + selectedCategory.name));
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.WITHER_SKELETON_SKULL, "Маніпуляція Катаклізмом", "10000", identifier);
    }
}
