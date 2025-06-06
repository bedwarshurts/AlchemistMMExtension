package me.bedwarshurts.mmextension.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(author = "bedwarshurts", name = "istrigger", aliases = {}, description = "Checks if the target is the trigger of the skill")
public class IsTriggerCondition implements ISkillMetaCondition {

    @Override
    public boolean check(SkillMetadata data) {
        for (AbstractEntity target : data.getEntityTargets()) {
            if (data.getTrigger().getUniqueId().equals(target.getUniqueId())) return true;
        }
        return false;
    }
}
