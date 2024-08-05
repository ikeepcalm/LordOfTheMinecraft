package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ChainControl extends Ability implements Listener {

    private boolean alleviateActive = false;
    private boolean concentrateActive = false;
    private Set<UUID> subordinates;

    private Category selectedCategory = Category.ALLEVIATION;
    private final Category[] categories = Category.values();
    private int selected = 0;

    public ChainControl(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    enum Category {
        SUBJUGATION("Підкорення"),
        ALLEVIATION("Розсердження"),
        CONCENTRATION("Концентрація");
        private final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        subordinates = pathway.getSequence().getSubordinates();

        switch (selectedCategory) {
            case SUBJUGATION -> subjugate();
            case ALLEVIATION -> alleviate();
            case CONCENTRATION -> concentrate();
        }
    }

    private void concentrate() {
        concentrateActive = !concentrateActive;
        if (concentrateActive) {
            alleviateActive = false;
        }

        player.sendMessage(Component.text("Ви " + (concentrateActive ? "активували" : "деактивували") + " концентрацію!").color(NamedTextColor.RED));
    }

    private void alleviate() {
        alleviateActive = !alleviateActive;
        if (alleviateActive) {
            concentrateActive = false;
        }
        player.sendMessage(Component.text("Ви " + (alleviateActive ? "активували" : "деактивували") + " розсердження!").color(NamedTextColor.RED));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (alleviateActive) {
            if (event.getEntity() instanceof Player damagedPlayer) {
                if (subordinates.contains(damagedPlayer.getUniqueId())) {
                    if (player != null && player.isOnline()) {
                        double damage = event.getDamage();
                        double redirectedDamage = damage * 0.5;
                        event.setDamage(damage * 0.5);
                        player.damage(redirectedDamage);
                        player.sendMessage(Component.text("Ви перенаправили урон свої підкорених на себе!").color(NamedTextColor.RED));
                    }
                }
            }
        } else if (concentrateActive) {
            if (event.getEntity() instanceof Player damagedPlayer) {
                if (damagedPlayer.getUniqueId().equals(player.getUniqueId())) {
                    double totalDamage = event.getDamage();
                    double individualDamage = totalDamage / subordinates.size();

                    for (UUID subordinateUUID : subordinates) {
                        Player subordinate = Bukkit.getPlayer(subordinateUUID);
                        if (subordinate != null && subordinate.isOnline()) {
                            subordinate.damage(individualDamage);
                        }
                    }

                    if (player != null && player.isOnline()) {
                        player.sendMessage(Component.text("Ви розподілили урон між своїми підкореними!").color(NamedTextColor.RED));
                    }

                    event.setCancelled(true);
                }
            }
        }

    }


    public void subjugate() {
        if (subordinates.size() >= pathway.getSequence().getMaxSubordinates()[pathway.getSequence().getCurrentSequence()]) {
            player.sendMessage(Component.text("Ви вже підкорили максимальну кількість гравців!").color(NamedTextColor.RED));
            return;
        }
        for (Entity entity : Objects.requireNonNull(player.getLocation().getWorld()).getNearbyEntities(player.getEyeLocation(), 5, 5, 5)) {
            if (entity instanceof LivingEntity) {
                if (entity instanceof Player target && entity != player) {
                    if (LordOfTheMinecraft.beyonders.containsKey(target.getUniqueId())) {
                        Beyonder beyonder = LordOfTheMinecraft.beyonders.get(target.getUniqueId());
                        if (pathway.getSequence().getCurrentSequence() < beyonder.getPathway().getSequence().getCurrentSequence()) {
                            takeCommandOf(target);
                        } else {
                            player.sendMessage(Component.text("Ви не можете підкорити цього гравця!").color(NamedTextColor.RED));
                        }
                    } else {
                        takeCommandOf(target);
                    }
                }
            }
        }
    }

    private void takeCommandOf(Player target) {
        if (!subordinates.contains(target.getUniqueId())) {
            subordinates.add(target.getUniqueId());
            if (player != null) {
                target.sendMessage(Component.text("Вас було підкорено! Ви тепер слугуєте " + player.getName() + "!").color(NamedTextColor.RED));
                player.sendMessage(Component.text("Ви підкорили " + target.getName() + "!").color(NamedTextColor.RED));
            }
        } else {
            subordinates.remove(target.getUniqueId());
            if (player != null) {
                target.sendMessage(Component.text("Вас було звільнено від кайданів підкорення!").color(NamedTextColor.RED));
                player.sendMessage(Component.text("Ви звільнили " + target.getName() + " від кайданів підкорення!").color(NamedTextColor.RED));
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player commander = event.getPlayer();
        if (player == null) {
            return;
        }

        if (commander.getUniqueId().equals(player.getUniqueId())) {
            for (UUID subordinateUUID : subordinates) {
                Player subordinate = Bukkit.getPlayer(subordinateUUID);
                if (subordinate != null && subordinate.isOnline()) {
                    event.message(event.message().color(NamedTextColor.RED));
                }
            }
        }
    }


    @Override
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
        player = pathway.getBeyonder().getPlayer();
    }

    @Override
    public void onHold() {
        if (player != null) {
            player.sendActionBar(Component.text("Обрано: ").color(NamedTextColor.BLUE).append(Component.text(selectedCategory.name).color(NamedTextColor.GOLD)));
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.DISC_FRAGMENT_5, "Субординація", "800", identifier);
    }
}
