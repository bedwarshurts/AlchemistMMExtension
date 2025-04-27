package me.bedwarshurts.mmextension.skills.mechanics.aura;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.util.Objects;

@MythicMechanic(author = "bedwarshurts", name = "cancelplayerdeath", aliases = {}, description = "Cancels the player's next death")
public class CancelPlayerDeathMechanic extends AlchemistAura implements ITargetedEntitySkill {

    private final double healthPercentage;
    private final MythicSkill skill;

    public CancelPlayerDeathMechanic(SkillExecutor manager, File file, MythicLineConfig mlc) {
        super(manager, file, mlc.getLine(), mlc);

        this.healthPercentage = mlc.getDouble("healthPercentage", 100.0);
        this.skill = new MythicSkill(mlc.getString("skill", ""));
    }
    
    @SuppressWarnings("resource")
    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;
        new CancelPlayerDeathMechanicTracker(target, data);
        return SkillResult.SUCCESS;
    }

    private class CancelPlayerDeathMechanicTracker extends AlchemistAura.AlchemistAuraTracker {
        private final AbstractEntity target;

        public CancelPlayerDeathMechanicTracker(AbstractEntity target, SkillMetadata data) {
            super(target, data);
            this.target = target;

            start();
        }

        @Override
        public void auraStart() {
            executeAuraSkill(onStartSkill, skillMetadata);
            Events.subscribe(PlayerDeathEvent.class, EventPriority.NORMAL)
                    .filter(e -> e.getEntity().getUniqueId().equals(target.getUniqueId()))
                    .handler(e -> {
                        e.setCancelled(true);
                        e.setReviveHealth(Objects.requireNonNull
                                (e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() * healthPercentage / 100.0);
                        skill.cast(skillMetadata);
                    }).bindWith(this);
        }

        @Override
        public void auraStop() {
            executeAuraSkill(onEndSkill, skillMetadata);
        }
    }
}