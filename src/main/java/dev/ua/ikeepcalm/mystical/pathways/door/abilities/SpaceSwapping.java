package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class SpaceSwapping extends Ability implements Listener {

    private int radius;
    private boolean isSwapping;

    private usages useCase;
    private final usages[] useCases;
    private int selected;

    private ArrayList<Block> swappedBlocks;
    private Location originLoc;

    public SpaceSwapping(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);

        radius = 30;
        isSwapping = false;

        useCases = usages.values();
        selected = 0;
        useCase = useCases[selected];
    }

    enum usages {
        SWAP("Поміняти місцями ділянки"),
        MOVE("Перемістити ділянку у нове місцезнаходження"),
        COPY("Скопіювати ділянку у нове місцезнаходження");

        public final String name;

        usages(String name) {
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        for (int i = 0; i < 80; i++) {
            if (loc.getBlock().getType().isSolid() || loc.getBlock().getType() == Material.WATER)
                break;
            loc.add(dir);
        }

        if (loc.getWorld() == null)
            return;

        if (!isSwapping) {
            isSwapping = true;

            swappedBlocks = GeneralPurposeUtil.getBlocksInSquare(loc.getBlock(), radius, false);

            originLoc = loc.clone();

            for (Block block : swappedBlocks) {
                if (!block.getLocation().add(0, 1, 0).getBlock().getType().isSolid() && block.getType().isSolid())
                    p.getWorld().spawnParticle(Particle.SPELL_WITCH, block.getLocation().clone().add(0, 1, 0), 2, 0, 0, 0, 0);
            }
            return;
        }

        if (loc.getWorld() != originLoc.getWorld()) {
            isSwapping = false;
            swappedBlocks = null;

            p.sendMessage("§cДві ділянки повинні знаходитись в одному вимірі!");
            return;
        }

        isSwapping = false;

        Vector subtract = loc.clone().toVector().subtract(originLoc.clone().toVector());

        ArrayList<Block> newBlocks = GeneralPurposeUtil.getBlocksInSquare(loc.getBlock(), radius, false);
        HashMap<Block, Material> materials = new HashMap<>();
        HashMap<Block, BlockData> blockDatas = new HashMap<>();

        for (Block block : newBlocks) {
            materials.put(block, block.getType());
            blockDatas.put(block, block.getBlockData());
        }

        Vector newVector = originLoc.clone().toVector().subtract(loc.clone().toVector());

        for (Block block : swappedBlocks) {
            block.getWorld().getBlockAt(block.getLocation().clone().add(subtract)).setType(block.getType());
            block.getWorld().getBlockAt(block.getLocation().clone().add(subtract)).setBlockData(block.getBlockData());

            if (useCase == usages.MOVE)
                block.setType(Material.AIR);
        }

        if (useCase == usages.SWAP) {
            for (Block block : newBlocks) {
                block.getWorld().getBlockAt(block.getLocation().clone().add(newVector)).setType(materials.get(block));
                block.getWorld().getBlockAt(block.getLocation().clone().add(newVector)).setBlockData(blockDatas.get(block));
            }
        }
    }

    @Override
    public void leftClick() {
        if (isSwapping) {
            p.sendMessage("§cЗараз ви міняєте місцями ділянки!", "§cЗміну місцями скасовано!");
            isSwapping = false;
            swappedBlocks = null;
            return;
        }
        selected++;

        if (selected >= useCases.length) {
            selected = 0;
        }

        useCase = useCases[selected];
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        p = pathway.getBeyonder().getPlayer();

        if (e.getPlayer() != p || e.getPlayer().isSneaking() || !p.getInventory().getItemInMainHand().isSimilar(getItem()) || isSwapping)
            return;

        radius++;

        if (radius >= 43)
            radius = 5;

        p.sendMessage("§bРадіус встановлено на " + radius);
    }

    @Override
    public void onHold() {
        p = pathway.getBeyonder().getPlayer();

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§bОбраний тип контролю: §7" + useCase.name));
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.SCULK_SENSOR, "Контроль Простору", "6250", identifier);
    }
}
