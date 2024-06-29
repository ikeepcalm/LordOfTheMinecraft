package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class Wandering extends Ability implements Listener {


    public Wandering(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        dimension = Dimension.REAL;
        dimensions = Dimension.values();
        selected = 0;
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        Location loc = p.getLocation();

        loc.setWorld(Bukkit.getWorld(dimension.id));

        for (int i = 0; i < 200; i++) {
            if (!loc.getBlock().getType().isSolid())
                break;

            loc.add(0, 1, 0);
        }

        p.teleport(loc);
        p.getWorld().spawnParticle(Particle.ENCHANT, p.getEyeLocation().clone().subtract(0, .5, 0), 250, .5, .75, .5, 0);
    }

    private enum Dimension {
        REAL("Реальний світ", "world"),
        SPIRIT("Вимір духів", "world_nether"),
        ASTRAL("Астральний вимір", "world_the_end");

        private final String name;
        private final String id;

        Dimension(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }

    private Dimension dimension;

    private final Dimension[] dimensions;
    private int selected;

    @Override
    public void leftClick() {
        selected++;

        if (selected >= dimensions.length)
            selected = 0;

        dimension = dimensions[selected];
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.ENDER_EYE, "Блукання Скрізь Світи", "2500", identifier);
    }

    @Override
    public void onHold() {
        p = pathway.getBeyonder().getPlayer();

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обраний вимір: " + dimension.name));
    }
}
