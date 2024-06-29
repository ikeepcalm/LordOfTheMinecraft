package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FindPlayer extends Ability implements Listener {

    private static boolean finding;

    public FindPlayer(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
        finding = false;
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        if (finding) {
            finding = false;
            p.sendMessage("§cСкасовано");
            return;
        }

        p.sendMessage("§bВведіть ім'я гравця, до якого хочете телепортуватися");
        p.sendMessage("§bНатисніть ПКМ знов, щоб скасувати");

        finding = true;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        p = pathway.getBeyonder().getPlayer();

        if (!finding || e.getPlayer() != p)
            return;

        e.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Player tp = null;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!e.getMessage().equalsIgnoreCase(player.getName()))
                        continue;
                    tp = player;
                    break;
                }

                finding = false;


                if (tp == null) {
                    p.sendMessage("§cГравця " + e.getMessage() + " не знайдено!");
                    p.sendMessage("§cСкасовано");
                    return;
                }

                p.teleport(tp);
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 0);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.RECOVERY_COMPASS, "Астральний Візит", "15000", identifier);
    }
}
