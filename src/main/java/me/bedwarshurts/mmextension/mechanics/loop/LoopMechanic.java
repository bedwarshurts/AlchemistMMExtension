package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;

import java.util.*;

@MythicMechanic(author = "bedwarshurts", name = "loop", aliases = {}, description = "Loop through a set of skills multiple times with a delay")
public class LoopMechanic implements INoTargetSkill {
    private final String condition;
    private final SkillExecutor skillExecutor;
    private final String skillName;
    private final PlaceholderDouble delayMs;
    private final String loopID;
    private final String onStart;
    private final String onEnd;
    private static final Map<String, LoopHandler> loopHandlers = new HashMap<>();

    public LoopMechanic(SkillExecutor manager, MythicLineConfig mlc) {
        this.condition = mlc.getString("condition", "0=0");
        this.skillExecutor = manager;
        this.skillName = mlc.getString("skill", "");
        this.delayMs = PlaceholderDouble.of(mlc.getString(new String[]{"interval", "i"}, "0"));
        this.loopID = mlc.getString("loopID", UUID.randomUUID().toString());
        this.onStart = mlc.getString(new String[]{"onStart", "oS"}, "");
        this.onEnd = mlc.getString(new String[]{"onEnd", "oE"}, "");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        if (!skillName.isEmpty()) {
            Optional<Skill> skillOptional = skillExecutor.getSkill(skillName);
            if (skillOptional.isPresent()) {
                Skill skillToExecute = skillOptional.get();
                LoopHandler loopHandler = new LoopHandler(skillToExecute, data, skillExecutor, condition, delayMs.get(data), loopID, this);
                loopHandlers.put(loopID, loopHandler);

                // Execute onStart skill
                SkillUtils.castSkill(skillExecutor, data, onStart);

                loopHandler.startLoop();

                return SkillResult.SUCCESS;
            }
        }
        return SkillResult.INVALID_CONFIG;
    }

    public static LoopHandler getLoopHandler(String loopID) {
        return loopHandlers.get(loopID);
    }

    public static void removeLoopHandler(String loopID) {
        loopHandlers.remove(loopID);
    }

    public String getOnEnd() {
        return this.onEnd;
    }
}