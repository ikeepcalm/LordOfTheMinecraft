package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class MirrorWorldTraversal extends Ability implements Listener {

    private boolean traversing;
    private GameMode prevGameMode;
    private boolean onCooldown;
    private final Material[] glassMaterials;
    private static final long MAX_TRAVERSAL_TIME_TICKS = 2000L;

    public MirrorWorldTraversal(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);

        glassMaterials = new Material[]{
                Material.GLASS,
                Material.BLACK_STAINED_GLASS,
                Material.BLACK_STAINED_GLASS_PANE,
                Material.GLASS_PANE,
                Material.WHITE_STAINED_GLASS,
                Material.WHITE_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.MAGENTA_STAINED_GLASS,
                Material.MAGENTA_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS,
                Material.LIME_STAINED_GLASS_PANE,
                Material.PINK_STAINED_GLASS,
                Material.PINK_STAINED_GLASS_PANE,
                Material.GRAY_STAINED_GLASS,
                Material.GRAY_STAINED_GLASS_PANE,
                Material.LIGHT_GRAY_STAINED_GLASS,
                Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS,
                Material.CYAN_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS,
                Material.PURPLE_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.BROWN_STAINED_GLASS,
                Material.BROWN_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS,
                Material.RED_STAINED_GLASS_PANE,
                Material.BLACK_STAINED_GLASS,
                Material.BLACK_STAINED_GLASS_PANE
        };

        traversing = false;
        onCooldown = false;
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        if (traversing) {
            return;
        }

        if (onCooldown)
            return;

        ArrayList<Block> nearBlocks = GeneralPurposeUtil.getBlocksInSquare(player.getEyeLocation().getBlock(), 4, false);

        boolean valid = false;
        for (Block block : nearBlocks) {
            if (!Arrays.asList(glassMaterials).contains(block.getType()))
                continue;

            valid = true;
            break;
        }

        if (!valid) {
            player.sendMessage("§cВи повинні бути поруч із дзеркалом / дзеркальними блоками!");
            return;
        }

        traversing = true;
        prevGameMode = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (traversing) {
                    player.sendMessage("§cРеальність розбивається навколо вас, як уламки дзеркала...");
                    player.setGameMode(prevGameMode);
                    player.damage(player.getHealth() / 2);
                    traversing = false;
                }
            }
        }.runTaskLater(LordOfTheMinecraft.instance, MAX_TRAVERSAL_TIME_TICKS);

        onCooldown = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                onCooldown = false;
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 20);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        player = pathway.getBeyonder().getPlayer();

        if (!traversing || e.getPlayer() != player)
            return;

        ArrayList<Block> nearBlocks = GeneralPurposeUtil.getBlocksInSquare(player.getEyeLocation().getBlock(), 4, false);

        boolean valid = false;
        for (Block block : nearBlocks) {
            if (!Arrays.asList(glassMaterials).contains(block.getType()))
                continue;

            valid = true;
            break;
        }

        if (onCooldown)
            return;

        if (!valid) {
            player.sendMessage("§cВи повинні бути поруч із дзеркалом / дзеркальними блоками!");
            return;
        }

        player.setGameMode(prevGameMode);
        traversing = false;
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.DIAMOND, "Мандрівка Через Задзеркалля", "300", identifier);
    }
}
