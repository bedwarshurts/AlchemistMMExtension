package me.bedwarshurts.mmextension.skills.mechanics.aura;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;

import java.io.File;

public abstract class AlchemistAura extends Aura {

    public AlchemistAura(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
    }

    public abstract class AlchemistAuraTracker extends Aura.AuraTracker {
        public AlchemistAuraTracker(AbstractEntity entity, SkillMetadata data) {
            super(entity, data);
        }

        @Override
        public boolean isValid() {
            if (hasEnded || components.hasTerminated()) return false;
            if (startCharges > 0 && chargesRemaining <= 0) return false;
            if (startDuration < 0) {
                return entity.isPresent() && entity.get().isValid();
            }
            return ticksRemaining > 0 && entity.isPresent() && entity.get().isValid();
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
            entity.ifPresent(e -> this.skillMetadata.setOrigin(e.getLocation()));
            this.auraTick();
        }
    }
}
