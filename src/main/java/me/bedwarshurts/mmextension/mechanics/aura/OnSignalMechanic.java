package me.bedwarshurts.mmextension.mechanics.aura;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.events.EventSubscriptionBuilder;
import me.bedwarshurts.mmextension.utils.events.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.io.File;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.plugin;

@MythicMechanic(author = "bedwarshurts", name = "onsignal", aliases = {}, description = "Triggers a skill when a player receives a signal")
public class OnSignalMechanic extends Aura implements ITargetedEntitySkill {
    private final MythicSkill skill;
    private final String signal;
    private EventSubscriptionBuilder<MythicPlayerSignalEvent> listener;

    public OnSignalMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);

        this.skill = new MythicSkill(mlc.getString(new String[]{"skill"}, ""));
        this.signal = mlc.getString(new String[]{"signal"}, "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;
        new OnSignalMechanicTracker(target, data);
        return SkillResult.SUCCESS;
    }

    private class OnSignalMechanicTracker extends AuraTracker {
        private final AbstractEntity target;

        public OnSignalMechanicTracker(AbstractEntity target, SkillMetadata data) {
            super(target, data);
            this.target = target;

            start();
        }

        @Override
        public void auraStart() {
            executeAuraSkill(onStartSkill, skillMetadata);
            listener = Events.subscribe(MythicPlayerSignalEvent.class, EventPriority.NORMAL).filter(event -> {
                Player eventPlayer = (Player) event.getProfile().getEntity().getBukkitEntity();
                return eventPlayer != null && eventPlayer.getUniqueId().equals(target.getUniqueId()) && event.getSignal().equals(signal);
            }).handler(event -> {
                skillMetadata.setTrigger(event.getProfile().getEntity());
                skill.cast(skillMetadata);
            }).bindWith(plugin);
        }

        @Override
        public void auraStop() {
            listener.unsubscribe();
            executeAuraSkill(onEndSkill, skillMetadata);
        }
    }
}