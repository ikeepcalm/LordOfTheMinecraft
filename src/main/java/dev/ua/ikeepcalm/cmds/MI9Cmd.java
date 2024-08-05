package dev.ua.ikeepcalm.cmds;

import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class MI9Cmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!s.hasPermission(new Permission("lordoftheminecraft.mi9"))) {
            s.sendMessage("§cВи не маєте дозволу на використання цієї команди!");
            return true;
        }

        if (!(s instanceof Player p)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }

        if (args[0].equalsIgnoreCase("monocle")) {
            ItemStack monocle = new ItemStack(Material.GLOWSTONE_DUST);
            ItemMeta meta = monocle.getItemMeta();
            meta.setDisplayName("§6Монокль");
            List<String> lore = List.of("§7Монокль для дослідження злочинів", "§7ПКМ - увімкнути режим дослідження", "§7ЛКМ - провести дослідження поруч");
            meta.setLore(lore);
            meta.setCustomModelData(201);
            monocle.setItemMeta(meta);
            NBT.modify(monocle, (nbt) -> {
                nbt.setBoolean("mi9Monocle", true);
            });

            if (!p.getInventory().contains(monocle)) {
                p.getInventory().addItem(monocle);
                p.sendMessage("§aВи отримали монокль!");
                return true;
            } else {
                p.sendMessage("§cУ вас вже є монокль!");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("stick")) {
            ItemStack stick = new ItemStack(Material.GLOWSTONE_DUST);
            ItemMeta meta = stick.getItemMeta();
            meta.setDisplayName("§6Палиця");
            meta.setCustomModelData(200);
            stick.setItemMeta(meta);
            NBT.modify(stick, (nbt) -> {
                nbt.setBoolean("mi9Stick", true);
            });

            if (!p.getInventory().contains(stick)) {
                p.getInventory().addItem(stick);
                p.sendMessage("§aВи отримали палицю!");
                return true;
            } else {
                p.sendMessage("§cУ вас вже є палиця!");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("exc") || args[0].equalsIgnoreCase("excommunicado")) {
            if (args.length == 2) {
                String nickname = args[1];
                List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
                if (excList.contains(nickname)) {
                    excList.remove(nickname);
                    s.sendMessage("§a" + nickname + " був видалений з розшуку!");
                } else {
                    excList.add(nickname);
                    s.sendMessage("§c" + nickname + " був доданий до розшуку!");
                }
                LordOfTheMinecraft.instance.getExcConfig().set("exc", excList);
                LordOfTheMinecraft.instance.saveExcConfig();
            } else {
                List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
                OfflinePlayer[] players = Bukkit.getOfflinePlayers();
                List<Item> items = new ArrayList<>();
                for (OfflinePlayer player : players) {
                    if (player.getName() == null) continue;

                    ItemStack itemStack = createHeadForPlayer(player);
                    ItemMeta meta = itemStack.getItemMeta();

                    if (excList.contains(player.getName())) {
                        meta.setDisplayName("§c" + player.getName());
                        meta.setLore(List.of("§cВ розшуку!"));
                    } else {
                        meta.setDisplayName("§a" + player.getName());
                        meta.setLore(List.of("§aНе в розшуку!"));
                    }
                    itemStack.setItemMeta(meta);
                    NBT.modify(itemStack, (nbt) -> {
                        nbt.setString("nickname", player.getName());
                        items.add(new SimpleItem(itemStack, e -> {
                            if (excList.contains(player.getName())) {
                                excList.remove(player.getName());
                                s.sendMessage("§a" + player.getName() + " був видалений з розшуку!");
                                e.getEvent().getView().close();
                            } else {
                                excList.add(player.getName());
                                s.sendMessage("§c" + player.getName() + " був доданий до розшуку!");
                                e.getEvent().getView().close();
                            }
                            LordOfTheMinecraft.instance.getExcConfig().set("exc", excList);
                            LordOfTheMinecraft.instance.saveExcConfig();
                        }));
                    });
                }
                Gui gui = PagedGui.items()
                        .setStructure(
                                "# # # # # # # # #",
                                "# x x x x x x x #",
                                "# x x x x x x x #",
                                "# # # < # > # # #")
                        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                        .addIngredient('#', GeneralItemsUtil.getMagentaPane())
                        .addIngredient('<', new BackItem())
                        .addIngredient('>', new ForwardItem())
                        .setContent(items)
                        .build();


                Window window = Window.single()
                        .setViewer(p)
                        .setTitle("§6Список розшукуваних")
                        .setGui(gui)
                        .build();
                window.open();
            }
        } else {
            p.sendMessage("§cНеправильне використання: Використовуйте /mi9 <option>! (Monocle, Stick, Excommunicado)");
        }

        return true;
    }

    public static @NotNull ItemStack createHeadForPlayer(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack createHeadForPlayer(OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static class BackItem extends PageItem {


        public BackItem() {
            super(false);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
                getGui().goBack();
            }
        }

        @Override
        public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
            ItemBuilder builder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
            builder.setDisplayName("§7Попередня сторінка")
                    .addLoreLines(pagedGui.hasPreviousPage()
                            ? "§7Перейти на сторінку §e" + pagedGui.getCurrentPage() + "§7/§e" + pagedGui.getPageAmount()
                            : "§cЦе перша сторінка");

            return builder;
        }
    }

    private static class ForwardItem extends PageItem {

        public ForwardItem() {
            super(true);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
                getGui().goForward();
            }
        }

        @Override
        public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
            ItemBuilder builder = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE);
            builder.setDisplayName("§7Наступна сторінка")
                    .addLoreLines(pagedGui.hasNextPage()
                            ? "§7Перейти на сторінку §e" + (pagedGui.getCurrentPage() + 2) + "§7/§e" + pagedGui.getPageAmount()
                            : "§cЦе остання сторінка");

            return builder;
        }
    }
}
