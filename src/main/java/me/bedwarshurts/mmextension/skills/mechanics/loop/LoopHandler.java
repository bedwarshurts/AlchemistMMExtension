package me.bedwarshurts.mmextension.skills.mechanics.loop;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import io.lumine.mythic.api.skills.SkillMetadata;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.bedwarshurts.mmextension.utils.terminable.Terminable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LoopHandler implements Terminable {

    private final MythicSkill skill;
    private BukkitTask task;
    private final SkillMetadata data;
    private final String condition;
    private final MythicSkill onEnd;

    public LoopHandler(String condition, MythicSkill skill, SkillMetadata data, long initialDelay, long delay, boolean synchronous, MythicSkill onEnd) {
        this.condition = condition;
        this.data = data;
        this.skill = skill;
        this.onEnd = onEnd;

        this.run(synchronous, initialDelay, delay);
    }

    private void run(boolean sync, long initialDelay, long delay) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!checkCondition(data)) {
                    close();
                    return;
                }
                skill.cast(data);
            }
        };
        if (sync) {
            this.task = task.runTaskTimer(AlchemistMMExtension.inst(), initialDelay, delay);
            return;
        }
        this.task = task.runTaskTimerAsynchronously(AlchemistMMExtension.inst(), initialDelay, delay);
    }

    private boolean checkCondition(SkillMetadata data) {
        String parsedCondition = PlaceholderUtils.parseDoublePlaceholders(condition, data);
        parsedCondition = PlaceholderUtils.parseIntPlaceholders(parsedCondition, data);
        Expression expression = new Expression(parsedCondition);

        try {
            return expression.evaluate().getBooleanValue();
        } catch (EvaluationException | ParseException e) {
            AlchemistMMExtension.inst().getLogger().severe("Error evaluating condition: " + parsedCondition
                    + " skill being looped: " + skill);
            return false;
        }
    }

    @Override
    public void close() {
        task.cancel();
        onEnd.cast(data);
    }
}
