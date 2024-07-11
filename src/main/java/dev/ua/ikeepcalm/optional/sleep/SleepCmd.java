package dev.ua.ikeepcalm.optional.sleep;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class SleepCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        String notification = LordOfTheMinecraft.instance.getConfig().getString("notification");
        String sleepMessage = LordOfTheMinecraft.instance.getConfig().getString("sleep-message");

        if (notification == null || sleepMessage == null) {
            return false;
        }

        if (notification.equals("CHAT") || notification.equals("ACTIONBAR")) {
            if (s instanceof Player player) {
                if (hasEnoughMoney(player)) {
                    List<Player> players = player.getWorld().getPlayers();
                    for (Player p : players) {
                        if (notification.equals("CHAT")) {
                            p.sendMessage(Component.text(sleepMessage.replace("{player}", player.getName())));
                        } else {
                            sendActionbar(p, sleepMessage.replace("{player}", player.getName()), 5);
                        }
                    }
                } else {
                    player.sendMessage(Component.text("Вартість виконання цієї команди становить 1 Лік!").color(TextColor.color(0xFF4680)));
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean hasEnoughMoney(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GOLD_INGOT) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasCustomModelData()) {
                    if (meta.getCustomModelData() == 2) {
                        if (item.getAmount() >= 1) {
                            item.setAmount(item.getAmount() - 1);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void sendActionbar(Player p, String message, int seconds) {
        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (time == seconds) {
                    cancel();
                } else {
                    time++;
                    p.sendActionBar(Component.text(message));
                }

            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 20);
    }
}
