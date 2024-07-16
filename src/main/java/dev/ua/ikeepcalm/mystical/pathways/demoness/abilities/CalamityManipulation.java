package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.entities.disasters.Blizzard;
import dev.ua.ikeepcalm.entities.disasters.Earthquake;
import dev.ua.ikeepcalm.entities.disasters.Tornado;
import dev.ua.ikeepcalm.entities.disasters.Tsunami;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CalamityManipulation extends Ability {

    private Category selectedCategory = Category.BLIZZARD;
    private final Category[] categories = Category.values();
    private int selected = 0;

    public CalamityManipulation(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
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
        player = pathway.getBeyonder().getPlayer();

        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location loc = player.getEyeLocation();

        for (int i = 0; i < 200; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);

        switch (selectedCategory) {
            case TORNADO -> new Tornado(player).spawnDisaster(player, loc);
            case BLIZZARD -> new Blizzard(player).spawnDisaster(player, loc);
            case TSUNAMI -> new Tsunami(player).spawnDisaster(player, loc);
            case EARTHQUAKE -> new Earthquake(player).spawnDisaster(player, loc);
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
        if (player == null)
            player = pathway.getBeyonder().getPlayer();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обраний катаклізм: §f" + selectedCategory.name));
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.WITHER_SKELETON_SKULL, "Маніпуляція Катаклізмом", "10000", identifier);
    }
}
