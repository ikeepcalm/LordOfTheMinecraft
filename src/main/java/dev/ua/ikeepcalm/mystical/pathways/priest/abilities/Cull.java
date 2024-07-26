package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Cull extends Ability implements Listener {

    private boolean isCullActive = false;

    public Cull(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        player.getInventory().removeItem(getItem());
        isCullActive = true;
        player.sendMessage("§aВаша наступна атака буде значно посилена!");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && attacker.equals(player) && isCullActive) {
            if (event.getEntity() instanceof LivingEntity target) {
                isCullActive = false;

                double targetHealth = target.getHealth();
                double damage;

                if (event.isCritical()) {
                    damage = targetHealth / 2.0;
                } else {
                    damage = targetHealth / 4.0;
                }

                player.sendMessage("§cВідбір успішно спрацював, наносячи " + damage + " шкоди!");
                event.setDamage(damage);
            }
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.POTION, "Відбір", "800", identifier);
    }
}

