package dev.ua.ikeepcalm.cmds;

import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MI9Cmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!s.hasPermission(new Permission("mi9items.use"))) {
            s.sendMessage("§cВи не маєте дозволу на використання цієї команди!");
            return true;
        }

        if (!(s instanceof Player p)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }

        if (args.length != 1) {
            s.sendMessage("§cНеправильне використання: Використовуйте /mi9 <option>! (Предмети: monocle, stick, Список розшукуваних: exc, excommunicado)");
            return true;
        }

        if (args[0].equalsIgnoreCase("monocle")) {
            ItemStack monocle = new ItemStack(Material.CLOCK);
            ItemMeta meta = monocle.getItemMeta();
            meta.setDisplayName("§6Монокль");
            meta.setCustomModelData(1);
            List<String> lore = List.of("§7Монокль для дослідження злочинів", "§7ЛКМ - змінити період дослідження", "§7ПКМ - провести дослідження");
            meta.setLore(lore);
            meta.setCustomModelData(3);
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
            ItemStack stick = new ItemStack(Material.STICK);
            ItemMeta meta = stick.getItemMeta();
            meta.setDisplayName("§6Палиця");
            meta.setCustomModelData(3);
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
            HashMap<UUID, Beyonder> beyonder = LordOfTheMinecraft.beyonders;
            List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
            List<Item> items = new ArrayList<>();
            for (Beyonder b : beyonder.values()) {
                System.out.println(b);
                ItemStack itemStack = createHeadForPlayer(b.getPlayer());
                ItemMeta meta = itemStack.getItemMeta();
                if (excList.contains(b.getPlayer().getName())) {
                    meta.setDisplayName("§c" + b.getPlayer().getName());
                    meta.setLore(List.of("§cВ розшуку!"));
                } else {
                    meta.setDisplayName("§a" + b.getPlayer().getName());
                    meta.setLore(List.of("§aНе в розшуку!"));
                }
                itemStack.setItemMeta(meta);
                NBT.modify(itemStack, (nbt) -> {
                    nbt.setString("nickname", b.getPlayer().getName());
                    items.add(new SimpleItem(itemStack, e -> {
                        if (excList.contains(b.getPlayer().getName())) {
                            excList.remove(b.getPlayer().getName());
                            p.sendMessage("§a" + b.getPlayer().getName() + " був видалений з розшуку!");
                            e.getEvent().getView().close();
                        } else {
                            excList.add(b.getPlayer().getName());
                            p.sendMessage("§c" + b.getPlayer().getName() + " був доданий до розшуку!");
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

    private static class BackItem extends PageItem {
        public BackItem() {
            super(false);
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
            super(false);
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
