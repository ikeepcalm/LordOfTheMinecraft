package dev.ua.ikeepcalm.optional.emporium.wrappers;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.optional.emporium.Advertiser;
import dev.ua.ikeepcalm.optional.emporium.EmporiumCmd;
import dev.ua.ikeepcalm.optional.emporium.managers.AdvertiserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.List;

public class CharWrapper extends AbstractItem {

    private Advertiser advertiser;
    private AdvertiserManager advertiserManager;
    private boolean enoughMoney;
    private ItemStack itemStack;
    private int sequence;
    private int pathway;
    private int price;

    public CharWrapper(Advertiser advertiser, AdvertiserManager advertiserManager, int pathway, int sequence) {
        this.advertiser = advertiser;
        this.advertiserManager = advertiserManager;
        this.sequence = sequence;
        this.pathway = pathway;
    }

    @Override
    public ItemProvider getItemProvider() {
        int basePrice = LordOfTheMinecraft.instance.getConfig().getInt("characteristic");
        Potion potion = LordOfTheMinecraft.instance.getPotions().get(pathway);
        itemStack = LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(sequence, potion.getName(), potion.getStringColor());
        double multiplier = 1 + (9 - sequence) * 2;
        price = (int) (basePrice * multiplier);

        if (advertiser.getBalance() >= price) {
            TextComponent name = Component.text("Вартість - " + price).color(TextColor.color(8, 255, 131));
            TextComponent copyrightLore = Component.text("© Emporium ©").color(TextColor.color(8, 131, 225));
            List<ComponentWrapper> lore = new ArrayList<>();
            lore.add(new AdventureComponentWrapper(copyrightLore));
            ItemProvider itemProvider = new ItemBuilder(itemStack).setDisplayName(new AdventureComponentWrapper(name)).setLore(lore);
            enoughMoney = true;
            return itemProvider;
        } else {
            TextComponent name = Component.text("Недостатньо балів").color(TextColor.color(255, 8, 131));
            enoughMoney = false;
            return new ItemBuilder(Material.BARRIER, 1).setDisplayName(new AdventureComponentWrapper(name));
        }
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        if (clickType.isLeftClick() || clickType.isRightClick()) {
            if (enoughMoney) {
                if (hasAvaliableSlot(player)) {
                    player.getInventory().addItem(itemStack);
                    advertiser.setBalance(advertiser.getBalance() - price);
                    advertiserManager.saveAdvertiser(advertiser);
                    EmporiumCmd.openMenu(player);
                }
            }
        }
    }

    public boolean hasAvaliableSlot(Player player) {
        Inventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }
}