package me.bedwarshurts.mmextension.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

public class TnTExplosionListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed tnt && tnt.hasMetadata("noBreak")) {
                for (MetadataValue value : tnt.getMetadata("noBreak")) {
                    if (value.asBoolean()) {
                        event.blockList().clear();
                        break;
                    }
                }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof TNTPrimed tnt && tnt.hasMetadata("noDamage")) {
                for (MetadataValue value : tnt.getMetadata("noDamage")) {
                    if (value.asBoolean()) {
                        event.setCancelled(true);
                        break;
                    }
                }
        }
    }
}