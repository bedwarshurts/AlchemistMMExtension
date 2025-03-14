package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlayerSkillCastListener implements Listener {

    @EventHandler
    public void onSkillCast(PlayerCastSkillEvent event) {
        Player player = event.getPlayer();
        PlayerData mythicPlayer = SkillUtils.getMythicPlayer(player);
        MythicPlayerSignalEvent signalEvent = new MythicPlayerSignalEvent(mythicPlayer, "onSkillCast");
        Bukkit.getPluginManager().callEvent(signalEvent);

        String skillName = event.getMetadata().getCast().getHandler().getId();
        mythicPlayer.getVariables().put("skillname", new StringVariable(skillName));

        double radius = 30.0;
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (MythicBukkit.inst().getAPIHelper().isMythicMob(entity)) {
                ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
                if (activeMob == null) continue;
                activeMob.signalMob(mythicPlayer.getEntity(), "onSkillCast");
            } else if (entity instanceof Player) {
                signalEvent = new MythicPlayerSignalEvent(SkillUtils.getMythicPlayer((Player) entity), "onSkillCast");
                Bukkit.getPluginManager().callEvent(signalEvent);
            }
        }
    }
}