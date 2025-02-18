package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.events.MythicTriggerEvent;
import me.bedwarshurts.mmextension.utils.FactionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        FactionUtils.applyExtraDamage(event);
    }
}