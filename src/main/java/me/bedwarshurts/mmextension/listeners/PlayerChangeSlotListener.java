package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.List;

public class PlayerChangeSlotListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeSlot(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        PlayerData mythicPlayer = SkillUtils.getMythicPlayer(player);
        if (mythicPlayer == null) return;

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