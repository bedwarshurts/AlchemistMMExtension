package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.List;
import java.util.Optional;

public class PlayerChangeSlotListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeSlot(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Optional<PlayerData> optionalMythicPlayer = MythicBukkit.inst().getPlayerManager().getProfile(player.getUniqueId());
        if (optionalMythicPlayer.isEmpty()) return;

        PlayerData mythicPlayer = optionalMythicPlayer.get();
        mythicPlayer.getVariables().put("previousSlot", new IntegerVariable(event.getPreviousSlot()));
        mythicPlayer.getVariables().put("nextSlot", new IntegerVariable(event.getNewSlot()));

        final double radius = 30;
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (!MythicBukkit.inst().getAPIHelper().isMythicMob(entity)) continue;

            ActiveMob mob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
            mob.signalMob(BukkitAdapter.adapt(event.getPlayer()), "onPlayerChangeSlot");
        }

        MythicPlayerSignalEvent signalEvent = new MythicPlayerSignalEvent(mythicPlayer, "onPlayerChangeSlot");
        Bukkit.getPluginManager().callEvent(signalEvent);
    }
}