package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Divine extends Ability {

    public Divine(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        LordOfTheMinecraft.instance.getDivination().divine(pathway.getBeyonder());
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.AMETHYST_SHARD, "Третє Око", "15", identifier);
    }
}
