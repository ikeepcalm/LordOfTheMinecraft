package dev.ua.ikeepcalm.optional.bossfights;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PortalListener implements Listener {
    public PortalListener() {
    }

    @EventHandler
    public void onPortalEnter(PlayerPortalEvent event) {
        if (event.getCause() == TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
            if (event.getPlayer().isConnected()) {
                LordOfTheMinecraft.instance.log("Trying to transfer player to the server");
                this.connect("bossfight", event.getPlayer());
            }
        }

    }

    private void connect(String name, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);
        player.sendPluginMessage(LordOfTheMinecraft.instance, "BungeeCord", out.toByteArray());
    }
}
