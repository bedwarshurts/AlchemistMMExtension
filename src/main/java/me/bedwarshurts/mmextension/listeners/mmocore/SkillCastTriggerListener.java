package me.bedwarshurts.mmextension.listeners.mmocore;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import me.bedwarshurts.mmextension.skills.triggers.MoreSkillTriggers;
import me.bedwarshurts.mmextension.skills.triggers.meta.SkillCastMeta;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class SkillCastTriggerListener implements Listener {
    private final EventExecutor eventExecutor = MythicBukkit.inst().getSkillManager().getEventBus();

    @EventHandler
    public void onSkillCastTrigger(PlayerCastSkillEvent event) {
        if (event.getMetadata().getTrigger().isPassive()) return;

        Player player = event.getPlayer();

        Location targetLocation;
        if (event.getMetadata().hasTargetLocation()) {
            targetLocation = event.getMetadata().getTargetLocation();
        } else {
            targetLocation = player.getLocation();
        }

        for (Entity entity : targetLocation.getNearbyEntities(30, 30, 30)) {
            if (MythicBukkit.inst().getAPIHelper().isMythicMob(entity)) {
                ActiveMob am = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);

                SkillCastMeta triggerMeta = new SkillCastMeta(event);
                SkillMetadata data = eventExecutor.buildSkillMetadata(MoreSkillTriggers.SKILL_CAST, triggerMeta, am,
                        BukkitAdapter.adapt(player), BukkitAdapter.adapt(player.getLocation()), true);
                TriggeredSkill ts = eventExecutor.processTriggerMechanics(data, triggerMeta);
                if (ts.getCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
