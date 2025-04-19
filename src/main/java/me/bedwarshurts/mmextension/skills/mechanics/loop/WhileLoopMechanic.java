package me.bedwarshurts.mmextension.skills.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;

@MythicMechanic(author = "bedwarshurts", name = "whileloop", aliases = {"while"}, description = "Executes a skill sequence in a while loop")
public class WhileLoopMechanic implements INoTargetSkill {
    private final String condition;
    private final MythicSkill skill;
    private final PlaceholderDouble delayMs;
    private final MythicSkill onStart;
    private final MythicSkill onEnd;
    private final boolean synchronous;

    public WhileLoopMechanic(MythicLineConfig mlc) {
        this.synchronous = mlc.getBoolean(new String[]{"sync", "s"}, false);
        this.condition = mlc.getString("condition", "");
        this.skill = new MythicSkill(mlc.getString("skill", ""));
        this.delayMs = PlaceholderDouble.of(mlc.getString(new String[]{"interval", "i"}, "0"));
        this.onStart = new MythicSkill(mlc.getString("onStart", ""));
        this.onEnd = new MythicSkill(mlc.getString("onEnd", ""));
    }

    @Override
    public SkillResult cast(SkillMetadata data) {

        onStart.cast(data);

        long tickInterval = Math.max(1, (long) (delayMs.get(data) / 50));
        data.setMetadata("handler", new LoopHandler(condition, skill, data, 0L, tickInterval, synchronous, onEnd));

        return SkillResult.SUCCESS;
    }
}