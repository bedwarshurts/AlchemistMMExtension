package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.Expression;

public class LoopHandler {
    private final String loopID;
    private final Skill skill;
    private final SkillMetadata data;
    private final String condition;
    private final double delay;
    private boolean shouldBreak;

    public LoopHandler(Skill skill, SkillMetadata data, String condition, double delay, String loopID) {
        this.loopID = loopID;
        this.skill = skill;
        this.data = data;
        this.condition = condition;
        this.delay = delay;
        this.shouldBreak = false;
    }

    public String getLoopID() {
        return loopID;
    }

    public void startLoop() {
        executeSkillWithDelay();
    }

    private void executeSkillWithDelay() {
        if (shouldBreak) return;

        String parsedCondition = PlaceholderUtils.parseDoublePlaceholders(condition, data);
        parsedCondition = PlaceholderUtils.parseIntPlaceholders(parsedCondition, data);
        Expression expression = new Expression(parsedCondition);

        if (expression.calculate() == 1) {
            skill.execute(data);
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), this::executeSkillWithDelay, (long) (delay / 50)); // Convert delay from milliseconds to ticks (50 ms = 1 tick)
        }
    }

    public void setShouldBreak(boolean shouldBreak) {
        this.shouldBreak = shouldBreak;
    }
}