package me.bedwarshurts.mmextension.mechanics.signal;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "onsignalremove", aliases = {}, description = "Removes a signal aura")
public class OnSignalRemoveMechanic implements ITargetedEntitySkill {
    private final String identifier;

    public OnSignalRemoveMechanic(MythicLineConfig mlc) {
        this.identifier = mlc.getString(new String[]{"identifier", "id", "name"}, "event");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        OnSignalMechanic.ACTIVE_SIGNALS.remove(identifier + target.getUniqueId());
        return SkillResult.SUCCESS;
    }
}
