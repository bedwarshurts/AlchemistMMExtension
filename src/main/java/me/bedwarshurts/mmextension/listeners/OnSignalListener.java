package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.bedwarshurts.mmextension.mechanics.OnSignalMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;

import java.util.List;

public class OnSignalListener implements Listener {

    @EventHandler
    public void onSignal(MythicPlayerSignalEvent event) {
        Player eventPlayer = BukkitAdapter.adapt(event.getProfile().getEntity());
        if (eventPlayer == null) return;

        List<String> skills = OnSignalMechanic.getActiveSkills(eventPlayer.getUniqueId(), event.getSignal());
        if (skills.isEmpty()) return;

        SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, event.getProfile(), BukkitAdapter.adapt(eventPlayer));

        for (String skillName : skills) {
            if (!(data.getEntityTargets() == null)) continue;

            data.setEntityTarget(event.getProfile().getEntity());
            SkillUtils.castSkill(data, skillName);
        }
    }
}