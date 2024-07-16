package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Hiding extends Ability implements Listener {

    private boolean hiding;

    public Hiding(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        items.addToSequenceItems(identifier - 1, sequence);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
        player = pathway.getBeyonder().getPlayer();
        player.setInvisible(false);
        player.setInvulnerable(false);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(LordOfTheMinecraft.instance, pathway.getBeyonder().getPlayer());
        }
    }

    @Override
    public void useAbility() {
        if (hiding)
            return;

        player = pathway.getBeyonder().getPlayer();

        hiding = true;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(LordOfTheMinecraft.instance, this.player);
        }

        player.setInvulnerable(true);
        player.setInvisible(true);

        new BukkitRunnable() {
            final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(216, 216, 216), 50f);

            @Override
            public void run() {

                player.spawnParticle(Particle.DUST, player.getLocation(), 500, 6, 6, 6, dust);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(LordOfTheMinecraft.instance, Hiding.this.player);
                }

                player.setInvulnerable(true);
                player.setInvisible(true);

                if (!hiding) {
                    cancel();
                    player.setInvisible(false);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.showPlayer(LordOfTheMinecraft.instance, pathway.getBeyonder().getPlayer());
                    }

                    player.setInvulnerable(false);
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 8);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getPlayer() != player || !hiding)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer() != player || !hiding)
            return;

        e.setCancelled(true);
    }

    @Override
    public void leftClick() {
        hiding = false;
    }

    @Override
    public void onHold() {
        if (hiding)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Натисніть ЛКМ щоб вийти із Завіси"));
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.LIGHT_GRAY_DYE, "Стрибок у Завісу Історії", "100", identifier);
    }

}
