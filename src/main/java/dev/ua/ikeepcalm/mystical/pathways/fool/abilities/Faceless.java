package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.cmds.MI9Cmd;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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

public class Faceless extends Ability {

    public Faceless(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        List<Item> items = new ArrayList<>();
        for (Player b : Bukkit.getOnlinePlayers()) {
            ItemStack itemStack = MI9Cmd.createHeadForPlayer(b);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§a" + b.getPlayer().getName());
            meta.setLore(List.of("§aПКМ - вкрасти лице"));
            itemStack.setItemMeta(meta);
            items.add(new SimpleItem(itemStack, e -> {
                Player player = e.getPlayer();
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                String name = skullMeta.getOwningPlayer().getName();
                if (name.equals(player.getName())) {
                    Disguise disguise = DisguiseAPI.getDisguise(player);
                    if (disguise != null) {
                        disguise.removeDisguise();
                        LordOfTheMinecraft.disguises.remove(player.getUniqueId());
                        player.sendMessage(Component.text("Вирішив передумати, еге ж?").color(TextColor.color(10, 250, 160)));
                        return;
                    }
                }
                PlayerDisguise playerDisguise = new PlayerDisguise(name);
                playerDisguise.setDisplayedInTab(false);
                playerDisguise.setKeepDisguiseOnPlayerDeath(false);
                playerDisguise.setNameVisible(false);
                playerDisguise.setEntity(player);
                playerDisguise.startDisguise();
                LordOfTheMinecraft.disguises.add(player.getUniqueId());
                if (this.player != null) {
                    this.player.sendMessage(Component.text("В який момент новий ти перестанеш бути собою?").color(TextColor.color(150, 255, 0)));
                }
            }));
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
            super(false);
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
