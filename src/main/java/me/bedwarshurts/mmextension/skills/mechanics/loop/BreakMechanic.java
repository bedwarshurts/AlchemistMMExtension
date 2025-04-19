package me.bedwarshurts.mmextension.skills.mechanics.loop;

import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.util.Optional;

@MythicMechanic(author = "bedwarshurts", name = "break", aliases = {}, description = "Breaks a while loop")
public class BreakMechanic implements INoTargetSkill {

    @Override
    public SkillResult cast(SkillMetadata data) {
        Optional<Object> loopIdObj = data.getMetadata("handler");

        loopIdObj.ifPresent(o -> {
            if (o instanceof LoopHandler loop) {
                loop.close();
            } else {
                throw new IllegalArgumentException("Invalid handler type: " + o.getClass().getName());
            }
        });
        return SkillResult.SUCCESS;
    }
}