package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "break", aliases = {}, description = "Breaks the loop in LoopMechanic")
public class BreakMechanic implements INoTargetSkill {
    private final String loopID;

    public BreakMechanic(MythicLineConfig mlc) {
        this.loopID = mlc.getString("loopID", "");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        LoopHandler loopHandler = LoopMechanic.getLoopHandler(loopID);
        if (loopHandler != null) {
            loopHandler.setShouldBreak(true);
            LoopMechanic.removeLoopHandler(loopID);
            return SkillResult.SUCCESS;
        }
        return SkillResult.INVALID_TARGET;
    }
}