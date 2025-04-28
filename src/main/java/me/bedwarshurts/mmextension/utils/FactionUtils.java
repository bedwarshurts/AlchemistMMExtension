package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.WeakHashMap;

public final class FactionUtils {

    private static final WeakHashMap<Entity, String> factionCache = new WeakHashMap<>();

    private FactionUtils() {
        throw new UnsupportedOperationException("You really shouldnt initialise this class");
    }

    private static String getFaction(Entity entity) {
        return factionCache.computeIfAbsent(entity, e -> {
            ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(e);
            return mob != null && mob.hasFaction() ? mob.getFaction() : null;
        });
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

        String[] factions = faction.toLowerCase().split(",");
        double damageResult = 0;

        for (String fac : factions) {
            String placeholder;
            switch (fac.trim()) {
                case "boss":
                    placeholder = PlaceholderAPI.setPlaceholders(player, "%mythiclib_raw_stat_boss_damage%");
                    damageResult += event.getDamage() * PlaceholderUtils.parseStatPlaceholder(placeholder) / 100;
                    break;
                default:
                    break;
            }
        }
        event.setDamage(event.getDamage() + damageResult);
    }
}