package dev.ua.ikeepcalm.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarUtil {
    private final Map<UUID, BossBar> bossBars;

    public BossBarUtil() {
        bossBars = new HashMap<>();
    }

    public void addPlayer(Player player, String title, BarColor color, BarStyle style, double progress) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bossBar);
    }

    public void removePlayer(Player player) {
        BossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removePlayer(player);
            bossBar.setVisible(false);
        }
    }

    public void setTitle(Player player, String title) {
        BossBar bossBar = bossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.setTitle(title);
        }
    }

    public void setProgress(Player player, double progress) {
        BossBar bossBar = bossBars.get(player.getUniqueId());
        if (progress < 0 || progress >= 1) {
            return;
        }
        if (bossBar != null) {
            if (progress < 0.3) {
                bossBar.setColor(BarColor.RED);
                bossBar.setStyle(BarStyle.SEGMENTED_10);
            } else if (progress < 0.5) {
                bossBar.setColor(BarColor.YELLOW);
                bossBar.setStyle(BarStyle.SOLID);
            } else if (progress >= 0.5) {
                bossBar.setColor(BarColor.BLUE);
                bossBar.setStyle(BarStyle.SOLID);
            }

            bossBar.setProgress(progress);
        }
    }
}
