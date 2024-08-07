package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DoorOpening extends Ability {
    public DoorOpening(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        Vector dir = player.getEyeLocation().getDirection();
        Location startLoc = player.getEyeLocation();

        for (int i = 0; i < 3; i++) {
            if (startLoc.getBlock().getType().isSolid())
                break;
            startLoc.add(dir);
        }

        if (!startLoc.getBlock().getType().isSolid())
            return;

        for (int i = 0; i < 100; i++) {
            if (startLoc.getBlock().getType() == Material.BEDROCK) {
                player.sendMessage("§cНадто низько...");
                return;
            }

            if (!startLoc.getBlock().getType().isSolid())
                break;

            startLoc.add(dir);
        }

        if (startLoc.getBlock().getType().isSolid())
            return;

        player.teleport(startLoc);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.OAK_DOOR, "Відкриття Воріт", "15", identifier);
    }
}
