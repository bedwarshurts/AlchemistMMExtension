package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Optional;

public class LoopHandler {
    private final String loopID;
    private final Skill skill;
    private final SkillMetadata data;
    private final SkillExecutor skillExecutor;
    private final String condition;
    private final double delay;
    private boolean shouldBreak;
    private final LoopMechanic mechanic;

    public LoopHandler(Skill skill, SkillMetadata data, SkillExecutor skillExecutor, String condition, double delay, String loopID, LoopMechanic mechanic) {
        this.loopID = loopID;
        this.skill = skill;
        this.data = data;
        this.skillExecutor = skillExecutor;
        this.condition = condition;
        this.delay = delay;
        this.shouldBreak = false;
        this.mechanic = mechanic;
    }

    public String getLoopID() {
        return loopID;
    }


    public void startLoop() {
        if (shouldBreak) {
            LoopMechanic.removeLoopHandler(loopID);
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> executeOnEndSkill(skillExecutor, mechanic.getOnEnd()), (long) (delay / 50));
            return;
        }

        String parsedCondition = PlaceholderUtils.parseDoublePlaceholders(condition, data);
        parsedCondition = PlaceholderUtils.parseIntPlaceholders(parsedCondition, data);
        Expression expression = new Expression(parsedCondition);

        if (expression.calculate() == 1) {
            skill.execute(data);
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), this::startLoop, (long) (delay / 50)); // Convert delay from milliseconds to ticks (50 ms = 1 tick)
        } else {
            LoopMechanic.removeLoopHandler(loopID);
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> executeOnEndSkill(skillExecutor, mechanic.getOnEnd()), (long) (delay / 50));
        }
    }

    public void setShouldBreak(boolean shouldBreak) {
        this.shouldBreak = shouldBreak;
    }

    private void executeOnEndSkill(SkillExecutor skillExecutor, String skillName) {
        if (!skillName.isEmpty()) {
            Optional<Skill> onEndSkill = skillExecutor.getSkill(skillName);
            onEndSkill.ifPresent(skill -> skill.execute(data));
        }
    }

    @Override
    public String toString() {
        return "LoopHandler{" +
                "loopID='" + loopID + '\'' +
                ", skill=" + skill +
                ", data=" + data +
                ", skillExecutor=" + skillExecutor +
                ", condition='" + condition + '\'' +
                ", delay=" + delay +
                ", shouldBreak=" + shouldBreak +
                ", mechanic=" + mechanic +
                '}';
    }

}