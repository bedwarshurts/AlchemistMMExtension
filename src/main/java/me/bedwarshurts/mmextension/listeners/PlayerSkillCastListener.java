package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlayerSkillCastListener implements Listener {

    @EventHandler
    public void onSkillCast(PlayerCastSkillEvent event) {
        Player player = event.getPlayer();
        String skillName = event.getMetadata().getCast().getHandler().getId();
        double radius = 30.0;
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (!MythicBukkit.inst().getAPIHelper().isMythicMob(entity)) continue;

            ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
            if (activeMob != null) {
                activeMob.getVariables().put("skillname", new StringVariable(skillName));
                activeMob.signalMob(BukkitAdapter.adapt(player), "onSkillCast");
            }
        }
    }
}