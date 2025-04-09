package me.bedwarshurts.mmextension.skills.mechanics.aura;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.io.File;

@MythicMechanic(author = "bedwarshurts", name = "onsignal", aliases = {}, description = "Triggers a skill when a player receives a signal")
public class OnSignalMechanic extends Aura implements ITargetedEntitySkill {
    private final MythicSkill skill;
    private final String signal;

    public OnSignalMechanic(SkillExecutor manager, File file, MythicLineConfig mlc) {
        super(manager, file, mlc.getLine(), mlc);

        this.skill = new MythicSkill(mlc.getString(new String[]{"skill"}, ""));
        this.signal = mlc.getString(new String[]{"signal"}, "");
    }

    @SuppressWarnings("resource")
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
            Events.subscribe(MythicPlayerSignalEvent.class, EventPriority.NORMAL).filter(event -> {
                Player eventPlayer = (Player) event.getProfile().getEntity().getBukkitEntity();
                return eventPlayer != null && eventPlayer.getUniqueId().equals(target.getUniqueId()) && event.getSignal().equals(signal);
            }).handler(event -> {
                skillMetadata.setTrigger(event.getProfile().getEntity());
                skill.cast(skillMetadata);
            }).bindWith(this);
        }

        @Override
        public void auraStop() {
            executeAuraSkill(onEndSkill, skillMetadata);
        }

        @Override
        public boolean isValid() {
            return this.entity.filter(abstractEntity ->
                    SkillUtils.isAuraValid(this.components, this.startDuration, this.chargesRemaining,
                            this.startCharges, this.ticksRemaining, abstractEntity, this.hasEnded)).isPresent();
        }

        @Override
        public void run() {
            if (this.startDuration >= 0) {
                this.ticksRemaining -= this.interval;
            }
            if (!this.isValid()) {
                this.terminate();
                return;
            }
            this.entity.ifPresent(e -> this.skillMetadata.setOrigin(e.getLocation()));
            this.auraTick();
        }
    }
}