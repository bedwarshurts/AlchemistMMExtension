package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import me.bedwarshurts.mmextension.mythic.MythicAuraRegistry;

public class AuraRemoveMechanic implements ITargetedEntitySkill {
    private final String identifier;

    public AuraRemoveMechanic(MythicLineConfig mlc) {
        this.identifier = mlc.getString(new String[]{"identifier", "id", "name"}, "event");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        MythicAuraRegistry aura = MythicAuraRegistry.getAura(identifier, target.getUniqueId());
        if (aura == null) return SkillResult.CONDITION_FAILED;
        aura.terminate();

        return SkillResult.SUCCESS;
    }
}
