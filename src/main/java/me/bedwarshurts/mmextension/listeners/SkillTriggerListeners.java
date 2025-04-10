package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.bedwarshurts.mmextension.skills.triggers.MoreSkillTriggers;
import me.bedwarshurts.mmextension.skills.triggers.meta.PlayerChangeSlotMeta;
import me.bedwarshurts.mmextension.skills.triggers.meta.PreAttackEntityMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class SkillTriggerListeners implements Listener {
    private final EventExecutor eventExecutor = MythicBukkit.inst().getSkillManager().getEventBus();

    @EventHandler
    public void onPreAttackEntityTrigger(PrePlayerAttackEntityEvent event) {
        Entity target = event.getAttacked();

        if (!MythicBukkit.inst().getMobManager().isActiveMob(BukkitAdapter.adapt(target))) return;
        ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
        Player player = event.getPlayer();

        PreAttackEntityMeta triggerMeta = new PreAttackEntityMeta(event);
        SkillMetadata data = eventExecutor.buildSkillMetadata(MoreSkillTriggers.PRE_ATTACK, triggerMeta, am,
                BukkitAdapter.adapt(player), BukkitAdapter.adapt(player.getLocation()), true);
        TriggeredSkill ts = eventExecutor.processTriggerMechanics(data, triggerMeta);
        if (ts.getCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChangeSlotTrigger(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        for (Entity entity : player.getNearbyEntities(30, 30, 30)) {
            if (!MythicBukkit.inst().getAPIHelper().isMythicMob(entity)) continue;
            ActiveMob am = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);

            PlayerChangeSlotMeta triggerMeta = new PlayerChangeSlotMeta(event);
            SkillMetadata data = eventExecutor.buildSkillMetadata(MoreSkillTriggers.PLAYER_CHANGE_SLOT, triggerMeta, am,
                    BukkitAdapter.adapt(player), BukkitAdapter.adapt(player.getLocation()), true);
            TriggeredSkill ts = eventExecutor.processTriggerMechanics(data, triggerMeta);
            if (ts.getCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}
