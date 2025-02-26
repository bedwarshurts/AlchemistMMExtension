package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.util.Optional;
import java.util.UUID;

@MythicMechanic(author = "bedwarshurts", name = "break", aliases = {}, description = "Breaks a loop")
public class BreakMechanic implements INoTargetSkill {

    @Override
    public SkillResult cast(SkillMetadata data) {
        Optional<Object> loopIdObj = data.getMetadata("whileLoopId");

        loopIdObj.ifPresent(uuid1 -> WhileLoopMechanic.stopLoop((UUID) uuid1));
        return SkillResult.SUCCESS;
    }
}