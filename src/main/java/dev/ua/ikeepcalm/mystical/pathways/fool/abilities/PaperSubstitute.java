package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PaperSubstitute extends Ability implements Listener {

    private boolean switching;

    public PaperSubstitute(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        items.addToSequenceItems(identifier - 1, sequence);

        switching = false;
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        if (switching) {
            player.sendMessage("§cВи вже використовуєте Техніку Підміни!");
            return;
        }

        //Check if Player has paper in inv
        if (!player.getInventory().contains(Material.PAPER)) {
            player.sendMessage("§cПотрібен папір!");
            return;
        }

        ItemStack item;
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            item = player.getInventory().getItem(i);
            if (item == null)
                continue;
            if (item.getType() == Material.PAPER) {
                item.setAmount(item.getAmount() - 1);
                player.getInventory().setItem(i, item);
                break;
            }
        }

        switching = true;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        player = pathway.getBeyonder().getPlayer();

        if (e.getEntity() != player || !switching)
            return;

        Location loc = player.getLocation();

        if (loc.getWorld() == null)
            return;

        e.setCancelled(true);
        switching = false;

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
        npc.addTrait(new RemoveOnDamageTrait());
        npc.spawn(loc);
        npc.setProtected(false);

        Random random = new Random();
        Location newLoc = loc.clone().add((random.nextInt(50) - 25), random.nextInt(25) - 12.5, random.nextInt(50) - 25);
        for (int i = 0; i < 500; i++) {
            if (!newLoc.getBlock().getType().isSolid())
                break;
            newLoc = loc.clone().add((random.nextInt(50) - 25), random.nextInt(25) - 12.5, random.nextInt(50) - 25);
        }
        player.teleport(newLoc);

        //remove FakePlayer after a few seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getWorld().spawnParticle(Particle.CLOUD, loc.clone().subtract(0, 0.25, 0), 100, 0.35, 1, 0.35, 0);
                npc.destroy();
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 60);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.ARMOR_STAND, "Техніка Підміни", "35", identifier);
    }
}

@TraitName("removeOnDamage")
class RemoveOnDamageTrait extends Trait {
    public RemoveOnDamageTrait() {
        super("removeOnDamage");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!npc.isSpawned())
            return;

        if (e.getEntity() == npc.getEntity())
            npc.destroy();
    }
}
