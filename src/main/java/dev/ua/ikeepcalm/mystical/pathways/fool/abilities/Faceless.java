package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.cmds.MI9Cmd;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

public class Faceless extends Ability {

    public Faceless(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        List<Item> items = new ArrayList<>();
        for (Player b : LordOfTheMinecraft.instance.getServer().getOnlinePlayers()) {
            ItemStack itemStack = MI9Cmd.createHeadForPlayer(b);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§a" + b.getPlayer().getName());
            meta.setLore(List.of("§aПКМ - вкрасти лице"));
            itemStack.setItemMeta(meta);
            items.add(new SimpleItem(itemStack));
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
                .setViewer(pathway.getBeyonder().getPlayer())
                .setTitle("§3Чого бажаєш ти, безликий?")
                .setGui(gui)
                .build();
        window.open();
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.EMERALD, "Безличчя", "300", identifier);
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
