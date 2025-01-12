package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.Bukkit;

public class FactionUtils {

    private static String getFaction(Entity entity) {
        ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
        return mob != null && mob.hasFaction() ? mob.getFaction() : null;
    }

    public static void applyExtraDamage(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent damageByEntityEvent)) {
            return;
        }

        Entity damager = damageByEntityEvent.getDamager();
        if (!(damager instanceof Player player)) {
            return;
        }

        Entity entity = event.getEntity();
        String faction = getFaction(entity);
        if (faction == null) {
            return;
        }

        double damageResult = 0;
        String[] factions = faction.toLowerCase().split(",");
        for (String fac : factions) {
            String placeholder;
            double extraDamage = 0;
            switch (fac.trim()) {
                case "boss":
                    placeholder = PlaceholderAPI.setPlaceholders(player, "%mythiclib_raw_stat_boss_damage%");
                    try {
                        extraDamage = Double.parseDouble(placeholder);
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Invalid placeholder value for %mythiclib_raw_stat_boss_damage%: " + placeholder);
                    }
                    damageResult += event.getDamage() * extraDamage / 100;
                    break;
                default:
                    break;
            }
        }
        event.setDamage(event.getDamage() + damageResult);
    }
}