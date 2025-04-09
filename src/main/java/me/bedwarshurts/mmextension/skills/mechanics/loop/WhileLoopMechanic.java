package me.bedwarshurts.mmextension.skills.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@MythicMechanic(author = "bedwarshurts", name = "whileloop", aliases = {"while"}, description = "Executes a skill sequence in a while loop")
public class WhileLoopMechanic implements INoTargetSkill {
    private final String condition;
    private final MythicSkill skill;
    private final PlaceholderDouble delayMs;
    private final MythicSkill onStart;
    private final MythicSkill onEnd;
    private final UUID loopId = UUID.randomUUID();

    private static final Map<UUID, BukkitTask> activeLoops = new ConcurrentHashMap<>();

    public WhileLoopMechanic(MythicLineConfig mlc) {
        this.condition = mlc.getString("condition", "");
        this.skill = new MythicSkill(mlc.getString("skill", ""));
        this.delayMs = PlaceholderDouble.of(mlc.getString(new String[]{"interval", "i"}, "0"));
        this.onStart = new MythicSkill(mlc.getString("onStart", ""));
        this.onEnd = new MythicSkill(mlc.getString("onEnd", ""));
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        data.setMetadata("whileLoopId", loopId);

        onStart.cast(data);

        long tickInterval = Math.max(1, (long) (delayMs.get(data) / 50));
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!checkCondition(data)) {
                    onEnd.cast(data);
                    cancel();
                    activeLoops.remove(loopId);
                    return;
                }
                skill.cast(data);
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0L, tickInterval);

        activeLoops.put(loopId, task);
        return SkillResult.SUCCESS;
    }


    public static void stopLoop(UUID id) {
        BukkitTask task = activeLoops.remove(id);
        if (task != null) {
            task.cancel();
        }
    }

    private boolean checkCondition(SkillMetadata data) {
        String parsedCondition = PlaceholderUtils.parseDoublePlaceholders(condition, data);
        parsedCondition = PlaceholderUtils.parseIntPlaceholders(parsedCondition, data);
        Expression expression = new Expression(parsedCondition);

        return expression.calculate() == 1;
    }
}