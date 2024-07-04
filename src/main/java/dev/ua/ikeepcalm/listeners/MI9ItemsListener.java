package dev.ua.ikeepcalm.listeners;

import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import net.coreprotect.CoreProtectAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class MI9ItemsListener implements Listener {

    private final HashMap<Player, Integer> time = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.isEmpty() || item.getType().isAir())
            return;
        if (NBT.get(item, (nbt) -> {
            return nbt.getBoolean("mi9Monocle");
        })) {
            if (player.hasPermission(new Permission("mi9items.use"))) {
                event.setCancelled(true);
                if (!time.containsKey(player)) {
                    time.put(player, 60);
                }

                if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
                    if (event.getClickedBlock() == null)
                        return;
                    if (event.getClickedBlock().getType().isAir())
                        return;
                    LordOfTheMinecraft.coreProtect.blockLookup(event.getClickedBlock(), time.get(player));
                }

                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    int instantTime = this.time.get(player) * 4;
                    int threeWeeks = 1_814_400;
                    if (instantTime > threeWeeks) {
                        time.put(player, 60);
                    } else {
                        if (instantTime > 60) {
                            time.put(player, instantTime);
                        }
                    }

                    instantTime = time.get(player);

                    if (instantTime >= 86400) {
                        instantTime = instantTime / 86400;
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Час дослідження слідів: " + instantTime + " дні(в)"));
                    } else if (instantTime > 3600) {
                        instantTime = instantTime / 3600;
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Час дослідження слідів: " + instantTime + " годин(и)"));
                    } else if (instantTime > 60) {
                        instantTime = instantTime / 60;
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Час дослідження слідів: " + instantTime + " хвилин(и)"));
                    }
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getClickedBlock() == null)
                        return;
                    if (event.getClickedBlock().getType().isAir())
                        return;
                    player.sendMessage("§bРезультати дослідження для: " + event.getClickedBlock().getType().name());
                    List<String[]> logs = LordOfTheMinecraft.coreProtect.blockLookup(event.getClickedBlock(), time.get(player));
                    for (String[] log : logs) {
                        CoreProtectAPI.ParseResult result = LordOfTheMinecraft.coreProtect.parseResult(log);
                        player.sendMessage("§f" + getTimeAgo(result.getTimestamp()) + " §7- §f" + result.getPlayer() + " §7- §f" + result.getActionString() + " §7- §f" + result.getType()
                                           + "§7 | §f" + result.getX() + " " + result.getY() + " " + result.getZ());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player victim = (Player) event.getEntity();
            if (event.getDamager().getType() == EntityType.PLAYER) {
                Player damager = (Player) event.getDamager();
                if (damager.hasPermission(new Permission("mi9items.use"))) {
                    ItemStack item = damager.getActiveItem();
                    if (item.getType().isAir())
                        return;

                    if (NBT.get(item, (nbt) -> {
                        return nbt.getBoolean("mi9Stick");
                    })) {
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2));
                    }
                }
            }
        }
    }

    private String getTimeAgo(long timestamp) {
        Instant then = Instant.ofEpochMilli(timestamp);
        Instant now = Instant.now();
        Duration duration = Duration.between(then, now);

        long seconds = Math.abs(duration.getSeconds());

        if (seconds < 60) {
            return formatSingleUnit(seconds, "секунд(и)");
        } else if (seconds < (60 * 60)) {
            long minutes = seconds / 60;
            return formatSingleUnit(minutes, "хвилин(и)");
        } else if (seconds < (24 * 60 * 60)) {
            long hours = seconds / (60 * 60);
            return formatSingleUnit(hours, "годин(и)");
        } else if (seconds < (365 * 24 * 60 * 60)) {
            long days = seconds / (24 * 60 * 60);
            return formatSingleUnit(days, "дні(в)");
        } else {
            long years = seconds / (365 * 24 * 60 * 60);
            return formatSingleUnit(years, "роки(ів)");
        }
    }

    private String formatSingleUnit(long value, String unit) {
        return String.format("%d %s тому", value, unit);
    }

}
