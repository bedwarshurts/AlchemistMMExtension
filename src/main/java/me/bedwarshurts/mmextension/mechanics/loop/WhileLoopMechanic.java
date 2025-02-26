package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@MythicMechanic(author = "bedwarshurts", name = "whileloop", aliases = {}, description = "Execute a skill in a loop")
public class WhileLoopMechanic implements INoTargetSkill {

    private static final Map<UUID, BukkitTask> activeLoops = new ConcurrentHashMap<>();
    private final String condition;
    private final String skillName;
    private final PlaceholderDouble delayMs;
    private final String onStart;
    private final String onEnd;
    private final UUID loopId = UUID.randomUUID();

    public WhileLoopMechanic(MythicLineConfig mlc) {
        this.condition = mlc.getString("condition", "");
        this.skillName = mlc.getString("skill", "");
        this.delayMs = PlaceholderDouble.of(mlc.getString(new String[]{"interval", "i"}, "0"));
        this.onStart = mlc.getString("onStart", "");
        this.onEnd = mlc.getString("onEnd", "");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        data.setMetadata("whileLoopId", loopId);

        SkillUtils.castSkill(data, onStart);

        long tickInterval = Math.max(1, (long) (delayMs.get(data) / 50));

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!checkCondition(data)) {
                    SkillUtils.castSkill(data, onEnd);
                    cancel();
                    activeLoops.remove(loopId);
                    return;
                }
                SkillUtils.castSkill(data, skillName);
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