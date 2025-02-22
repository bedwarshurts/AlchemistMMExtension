package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerChangeSlotListener implements Listener {

    @EventHandler
    public void onPlayerChangeSlot(PlayerInventorySlotChangeEvent event) {
        Player player = event.getPlayer();
        PlayerData mythicPlayer = MythicBukkit.inst().getPlayerManager().getProfile(player);

        MythicPlayerSignalEvent signalEvent = new MythicPlayerSignalEvent(mythicPlayer, "onPlayerChangeSlot");
        Bukkit.getPluginManager().callEvent(signalEvent);

        final double radius = 30;
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (!MythicBukkit.inst().getAPIHelper().isMythicMob(entity)) continue;

            ActiveMob mob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
            mob.signalMob(BukkitAdapter.adapt(event.getPlayer()), "onPlayerChangeSlot");

        }

        mythicPlayer.getVariables().put("previousSlot", new IntegerVariable(event.getRawSlot()));
        mythicPlayer.getVariables().put("nextSlot", new IntegerVariable(event.getPlayer().getInventory().getHeldItemSlot()));

        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            mythicPlayer.getVariables().remove("previousSlot");
            mythicPlayer.getVariables().remove("nextSlot");
        }, 20L);
    }
}