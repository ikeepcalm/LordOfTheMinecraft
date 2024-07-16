package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Instigate extends Ability {

    private boolean isInstigating;
    private Mob attacker;

    public Instigate(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        isInstigating = false;
        attacker = null;
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location loc = player.getEyeLocation();
        if (loc.getWorld() == null)
            return;

        LivingEntity target = null;

        outerloop:
        for (int i = 0; i < 25; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if (!(entity instanceof LivingEntity e) || entity == player)
                    continue;
                target = e;
                break outerloop;
            }

            loc.add(dir);
        }

        if (target == null) {
            player.sendMessage("§cЦіль не знайдено!");
            return;
        }

        if (!isInstigating) {
            if (!(target instanceof Mob mob)) {
                player.sendMessage("§cЦя сутність не може бути підбурена");
                return;
            }
            isInstigating = true;
            player.sendMessage("§aПідбурюю " + mob.getName() + "!");
            player.sendMessage("§aОберіть ціль");
            attacker = mob;
            return;
        }

        isInstigating = false;
        if (attacker == null) {
            player.sendMessage("§cЩось пішло не так!");
            return;
        }

        attacker.setTarget(target);
        player.sendMessage("§a" + target.getName() + " тепер є ціллю для " + attacker.getName() + "!");
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.CYAN_CANDLE, "Підбурювання", "30", identifier);
    }
}
