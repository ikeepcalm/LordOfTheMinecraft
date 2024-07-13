package dev.ua.ikeepcalm.optional.emporium;

import dev.ua.ikeepcalm.cmds.MI9Cmd;
import dev.ua.ikeepcalm.optional.emporium.managers.AdvertiserManager;
import dev.ua.ikeepcalm.optional.emporium.managers.EmporiumManager;
import dev.ua.ikeepcalm.optional.emporium.wrappers.CharWrapper;
import dev.ua.ikeepcalm.optional.emporium.wrappers.ItemWrapper;
import dev.ua.ikeepcalm.optional.emporium.wrappers.PotionWrapper;
import dev.ua.ikeepcalm.optional.emporium.wrappers.RecipeWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class EmporiumCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (args.length > 0) {
            if (s instanceof ConsoleCommandSender console) {
                if (args[0].equalsIgnoreCase("add")) {
                    String nickname = args[1];
                    AdvertiserManager advertiserDao = new AdvertiserManager();
                    if (advertiserDao.existsByNickname(nickname)) {
                        try {
                            int amount = Integer.parseInt(args[2]);
                            if (amount < 0) {
                                console.sendMessage("§cКількість не може бути від'ємною!");
                                return true;
                            }
                            Advertiser advertiser = advertiserDao.findByNickname(nickname);
                            advertiser.setBalance(advertiser.getBalance() + amount);
                            advertiserDao.saveAdvertiser(advertiser);
                            console.sendMessage("§aГравцю " + nickname + " було додано " + amount + " балів!");
                        } catch (NumberFormatException e) {
                            console.sendMessage("§cВведіть коректну кількість!");
                        }
                    } else {
                        console.sendMessage("§cГравець не зареєстрований в системі Емпоріуму!");
                        return true;
                    }
                }
            } else if (s instanceof RemoteConsoleCommandSender remoteConsole) {
                if (args[0].equalsIgnoreCase("add")) {
                    String nickname = args[1];
                    AdvertiserManager advertiserDao = new AdvertiserManager();
                    if (advertiserDao.existsByNickname(nickname)) {
                        try {
                            int amount = Integer.parseInt(args[2]);
                            if (amount < 0) {
                                remoteConsole.sendMessage("§cКількість не може бути від'ємною!");
                                return true;
                            }
                            Advertiser advertiser = advertiserDao.findByNickname(nickname);
                            advertiser.setBalance(advertiser.getBalance() + amount);
                            advertiserDao.saveAdvertiser(advertiser);
                            remoteConsole.sendMessage("§aГравцю " + nickname + " було додано " + amount + " балів!");
                        } catch (NumberFormatException e) {
                            remoteConsole.sendMessage("§cВведіть коректну кількість!");
                        }
                    } else {
                        remoteConsole.sendMessage("§cГравець не зареєстрований в системі Емпоріуму!");
                        return true;
                    }
                }
            } else {
                return true;
            }
        } else {
            if (!(s instanceof Player p)) {
                s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
                return true;
            }
            openMenu(p);
            return true;
        }
        return true;
    }

    public static void openMenu(Player player) {
        AdvertiserManager advertiserDao = new AdvertiserManager();
        EmporiumManager emporiumManager = new EmporiumManager();
        if (advertiserDao.existsByNickname(player.getName())) {
            Advertiser advertiser = advertiserDao.findByNickname(player.getName());
            Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r"));
            TextComponent windowComponent = Component.text("Емпоріум").color(TextColor.color(255, 8, 131));
            List<Item> items = new ArrayList<>();
            List<ItemWrapper> emporiumItems = emporiumManager.getEmporiumItems();
            if (emporiumItems.isEmpty()) {
                emporiumManager.initializeEmporium();
                emporiumItems = emporiumManager.getEmporiumItems();
            }

            for (ItemWrapper itemInfo : emporiumItems) {
                switch (itemInfo.getType()) {
                    case "CharWrapper" ->
                            items.add(new CharWrapper(advertiser, advertiserDao, itemInfo.getPathway(), itemInfo.getSequence()));
                    case "PotionWrapper" ->
                            items.add(new PotionWrapper(advertiser, advertiserDao, itemInfo.getPathway(), itemInfo.getSequence()));
                    case "RecipeWrapper" ->
                            items.add(new RecipeWrapper(advertiser, advertiserDao, itemInfo.getPathway(), itemInfo.getSequence()));
                }
            }

            Gui gui = PagedGui.items()
                    .setStructure(
                            "# # # # p # # # #",
                            "# x x x x x x x #",
                            "# x x x x x x x #",
                            "# x x x x x x x #",
                            "# # # # # # # # #")
                    .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                    .addIngredient('#', border)
                    .addIngredient('p', new BalanceItem(advertiser, player))
                    .setContent(items)
                    .build();

            Window window = Window.single()
                    .setViewer(player)
                    .setGui(gui)
                    .setTitle(new AdventureComponentWrapper(windowComponent))
                    .build();
            window.open();
        } else {
            Advertiser advertiser = new Advertiser(player.getName(), 0);
            advertiserDao.saveAdvertiser(advertiser);
            player.sendMessage("§aВи зареєструвались в системі Емпоріуму!");
        }
    }

    private static class BalanceItem extends AbstractItem {

        private final Advertiser advertiser;
        private final Player player;

        public BalanceItem(Advertiser advertiser, Player player) {
            this.advertiser = advertiser;
            this.player = player;
        }

        @Override
        public ItemProvider getItemProvider() {
            ItemBuilder builder = new ItemBuilder(MI9Cmd.createHeadForPlayer(player));
            TextComponent balance = Component.text("Баланс:").color(TextColor.color(255, 70, 131));
            TextComponent value = Component.text("$" + advertiser.getBalance()).color(TextColor.color(190, 70, 131));
            builder.setDisplayName(new AdventureComponentWrapper(balance)).addLoreLines(new AdventureComponentWrapper(value));
            return builder;
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        }
    }
}
