package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FogOfHistory extends Ability implements Listener {

    @Getter
    private final ArrayList<ItemStack> items;
    private final ArrayList<ItemStack> summonedItems;
    private ArrayList<Inventory> pages;

    private final Set<ItemStack> hashSet;

    private final ItemStack arrow;
    private final ItemStack barrier;

    private boolean active;

    private int currentPage;

    public FogOfHistory(int identifier, Pathway pathway, int sequence, Items itemsClass) {
        super(identifier, pathway, sequence, itemsClass);

        itemsClass.addToSequenceItems(identifier - 1, sequence);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);

        items = new ArrayList<>();
        hashSet = new HashSet<>();

        active = true;

        //Adding Inventory ItemStacks to List
        for (ItemStack item : pathway.getBeyonder().getPlayer().getInventory().getContents()) {
            if (item == null)
                continue;
            ItemStack addItem = item.clone();
            addItem.setAmount(1);
            addItem = normalizeItem(addItem);
            items.add(addItem);
        }
        summonedItems = new ArrayList<>();

        //initializing ItemStacks for Inventory
        barrier = new ItemStack(Material.BARRIER);
        ItemMeta tempMeta = barrier.getItemMeta();
        assert tempMeta != null;
        tempMeta.setDisplayName("§aПопередня сторінка");
        tempMeta.addEnchant(Enchantment.LUCK, 1, true);
        tempMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        barrier.setItemMeta(tempMeta);

        arrow = new ItemStack(Material.ARROW);
        tempMeta.setDisplayName("§aНаступна сторінка");
        arrow.setItemMeta(tempMeta);

        currentPage = 0;

        if (LordOfTheMinecraft.fogOfHistories.containsKey(pathway.getBeyonder().getUuid()))
            return;
        LordOfTheMinecraft.fogOfHistories.put(pathway.getBeyonder().getUuid(), this);
    }


    @EventHandler
    public void onPlayerPickUpItem(EntityPickupItemEvent e) {
        if (e.getEntity() != pathway.getBeyonder().getPlayer() || !active)
            return;

        ItemStack itemNormalized = normalizeItem(e.getItem().getItemStack().clone());

        boolean isContained = false;

        items.removeAll(Collections.singleton(null));

        items.removeIf(item -> (pathway.getSequence().checkValid(item)));


        for (ItemStack item : items) {
            if (item == null)
                continue;
            if (normalizeItem(item.clone()).isSimilar(itemNormalized))
                isContained = true;
        }

        if (!isContained) {
            ItemStack addItem = e.getItem().getItemStack().clone();
            addItem.setAmount(1);
            addItem = normalizeItem(addItem);
            items.add(addItem);
        }

    }

    private ItemStack normalizeItem(ItemStack itemNormalized) {
        if (itemNormalized instanceof Damageable) {
            ((Damageable) itemNormalized).setDamage(0);
        }
        return itemNormalized;
    }

    @Override
    public void useAbility() {
        items.addAll(hashSet);
        hashSet.clear();

        if (!active)
            return;

        Player p = pathway.getBeyonder().getPlayer();

        currentPage = 0;

        Set<ItemStack> tempSet = new HashSet<>(items);
        items.clear();
        items.addAll(tempSet);


        items.removeAll(Collections.singleton(null));

        items.removeIf(item -> (pathway.getSequence().checkValid(item)));
        items.removeIf(item -> GeneralItemsUtil.returnAllItems().contains(item));

        double pageCount = Math.ceil((float) items.size() / 52);


        if (pageCount == 0)
            pageCount = 1;

        pages = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            pages.add(Bukkit.createInventory(p, 54, "§5Завіса Історії"));
        }

        int counter = 0;
        for (int i = 0; i < pageCount; i++) {
            for (int j = 0; j < 52; j++) {
                if (counter >= items.size())
                    break;
                pages.get(i).setItem(j, items.get(counter));
                counter++;
            }
        }
        for (Inventory inv : pages) {
            inv.setItem(52, barrier);
            inv.setItem(53, arrow);
        }

        p.openInventory(pages.get(0));

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!active)
            return;
        ItemStack checkItem = e.getItemDrop().getItemStack().clone();

        boolean contains = false;
        for (ItemStack item : summonedItems) {
            if (item.isSimilar(checkItem))
                contains = true;
        }

        if (!contains)
            return;

        summonedItems.remove(checkItem);
        e.getItemDrop().remove();
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (!active)
            return;
        if (pages == null)
            return;
        if (!pages.contains(e.getInventory()))
            return;

        e.setCancelled(true);
        int maxSize = pathway.getSequence().getCurrentSequence() == 3 ? 3 : pathway.getSequence().getCurrentSequence() == 2 ? 9 : 12;
        if (summonedItems.size() >= maxSize) {
            return;
        }
        if (e.getCurrentItem() == null)
            return;

        if (e.getCurrentItem().isSimilar(barrier)) {
            if (currentPage <= 0)
                return;
            currentPage -= 1;
            e.getWhoClicked().openInventory(pages.get(currentPage));
            return;
        }

        if (e.getCurrentItem().isSimilar(arrow)) {
            currentPage += 1;
            if (currentPage >= pages.size()) {
                currentPage -= 1;
                return;
            }
            e.getWhoClicked().openInventory(pages.get(currentPage));
            return;
        }


        ItemStack summonedItem = e.getCurrentItem().clone();
        summonedItem.setAmount(1);
        e.getWhoClicked().getInventory().addItem(summonedItem);
        summonedItems.add(summonedItem);
        new BukkitRunnable() {
            @Override
            public void run() {
                summonedItems.remove(summonedItem);
                if (e.getWhoClicked().getInventory().contains(summonedItem))
                    e.getWhoClicked().getInventory().remove(summonedItem);
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 20 * 60 * 3);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.QUARTZ, "Завіса Історії", "100", identifier);
    }

    public void addItem(ItemStack item) {
        hashSet.add(item);
    }

    @Override
    public void removeAbility() {
        active = false;
    }
}
