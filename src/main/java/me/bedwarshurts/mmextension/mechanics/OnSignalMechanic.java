package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import java.util.*;

public class OnSignalMechanic implements INoTargetSkill {
    private static final Map<String, Long> ACTIVE_SIGNALS = new HashMap<>();
    private final String skill;
    private final String signal;
    private final long durationTicks;

    public OnSignalMechanic(MythicLineConfig mlc) {
        this.skill = mlc.getString(new String[]{"skill"}, "");
        this.signal = mlc.getString(new String[]{"signal"}, "");
        this.durationTicks = (long) mlc.getDouble(new String[]{"duration", "d"}, 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean success = false;

        for (AbstractEntity entity : data.getEntityTargets()) {
            if (!entity.isPlayer()) continue;
            success = true;

            UUID playerId = entity.getUniqueId();
            String signalKey = playerId + ":" + signal.toLowerCase() + ":" + skill;
            ACTIVE_SIGNALS.put(signalKey, System.currentTimeMillis() + (durationTicks * 50));
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }

    public static List<String> getActiveSkills(UUID playerId, String signal) {
        String prefix = playerId + ":" + signal + ":";
        List<String> result = new ArrayList<>();
        for (String key : new HashSet<>(ACTIVE_SIGNALS.keySet())) {
            if (key.startsWith(prefix)) {
                Long expires = ACTIVE_SIGNALS.get(key);
                if (expires != null && System.currentTimeMillis() <= expires) {
                    result.add(key.substring(prefix.length()));
                } else {
                    ACTIVE_SIGNALS.remove(key);
                }
            }
        }
        return result;
    }
}