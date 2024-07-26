package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Provoke extends Ability {

    public Provoke(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        Location loc = player.getLocation();
        final int random = (int) (Math.random() * 3);

        for (Entity entity : Objects.requireNonNull(player.getEyeLocation().getWorld()).getNearbyEntities(loc, 5, 5, 5)) {
            if (entity instanceof LivingEntity livingEntity) {
                if (entity instanceof Player target && entity != player) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 1, true, true));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 10, 0, true, true));
                    if (random % 2 == 0) {
                        ItemStack item = target.getInventory().getItemInMainHand();
                        if (item.getType() != Material.AIR) {
                            target.getInventory().removeItem(item);
                        }
                        Item droppedItem = target.getWorld().dropItemNaturally(target.getLocation(), item);
                        droppedItem.setPickupDelay(60);
                        droppedItem.setFallDistance(2);
                    } else {
                        shuffleInventory(target);
                    }

                    target.sendTitle(ChatColor.RED + "Провокація!", ChatColor.RED + "Лайно! Я вб'ю його!", 10, 40, 10);
                    if (target.hasPotionEffect(PotionEffectType.WITHER) && pathway.getSequence().getCurrentSequence() >= 4) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 2, true, true));
                    }
                    break;
                } else {
                    if (livingEntity != player) {
                        livingEntity.setFireTicks(80);
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15, 0, true, true));
                        player.sendMessage("§4" + livingEntity.getName() + " був спровокований!");
                        break;
                    }
                }
            }
        }
    }

    private void shuffleInventory(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : contents) {
            if (item != null) {
                itemList.add(item);
            }
        }
        Collections.shuffle(itemList);
        inventory.clear();
        for (int i = 0; i < itemList.size(); i++) {
            inventory.setItem(i, itemList.get(i));
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.RED_DYE, "Провокація", "40", identifier);
    }
}