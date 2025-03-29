package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import me.bedwarshurts.mmextension.mechanics.signal.OnSignalData;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.bedwarshurts.mmextension.mechanics.signal.OnSignalMechanic;

import java.util.LinkedList;

public class OnSignalListener implements Listener {

    @EventHandler
    public void onSignal(MythicPlayerSignalEvent event) {
        Player eventPlayer = BukkitAdapter.adapt(event.getProfile().getEntity());
        if (eventPlayer == null) return;

        LinkedList<OnSignalData> onSignalList = new LinkedList<>(OnSignalMechanic.ACTIVE_SIGNALS.values().stream()
                .filter(data -> data.getPlayer().equals(eventPlayer))
                .toList());

        if (onSignalList.isEmpty()) return;

        for (OnSignalData signal : onSignalList) {
            if (signal.getSignal().equals(event.getSignal())) {
                SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, signal.getCaster(), BukkitAdapter.adapt(eventPlayer));
                MythicSkill skill = signal.getSkill();
                skill.cast(data);
            }
        }

    }
}