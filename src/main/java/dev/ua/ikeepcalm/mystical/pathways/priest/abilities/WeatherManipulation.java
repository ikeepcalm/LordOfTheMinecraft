package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.entities.disasters.Blizzard;
import dev.ua.ikeepcalm.entities.disasters.Tornado;
import dev.ua.ikeepcalm.entities.disasters.Wildfire;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WeatherManipulation extends Ability {

    private Category selectedCategory = Category.BLIZZARD;
    private final Category[] categories = Category.values();
    private int selected = 0;

    public WeatherManipulation(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    enum Category {
        TORNADO("Торнадо"),
        BLIZZARD("Завірюха"),
        WILDFIRE("Пожежа");

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
            case WILDFIRE -> new Wildfire().spawnDisaster(player, loc);
        }
    }

    @Override
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
        player = pathway.getBeyonder().getPlayer();
    }

    @Override
    public void onHold() {
        if (player != null) {
            player.sendActionBar(Component.text("Обрана погода: ").color(NamedTextColor.BLUE).content(selectedCategory.name).color(NamedTextColor.GOLD));
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.CLOCK, "Маніпуляція Погодою", "6000", identifier);
    }
}
