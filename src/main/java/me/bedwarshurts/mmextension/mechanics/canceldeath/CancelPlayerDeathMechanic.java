package me.bedwarshurts.mmextension.mechanics.canceldeath;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MythicMechanic(author = "bedwarshurts", name = "cancelplayerdeath", aliases = {}, description = "Cancels the player's next death")
public final class CancelPlayerDeathMechanic implements ITargetedEntitySkill {
    private final double healthPercentage;
    private final String skillName;

    public static final Map<UUID, PlayerDeathData> playerDeathDatas = new HashMap<>();

    public CancelPlayerDeathMechanic(MythicLineConfig mlc) {
        this.healthPercentage = mlc.getDouble("healthPercentage", 100.0);
        this.skillName = mlc.getString("skill", "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!(target.getBukkitEntity() instanceof Player player)) return SkillResult.INVALID_TARGET;

        if (playerDeathDatas.containsKey(player.getUniqueId())) {
            playerDeathDatas.remove(player.getUniqueId());

            return SkillResult.SUCCESS;
        }
        playerDeathDatas.put(player.getUniqueId(), new PlayerDeathData(player.getUniqueId(), healthPercentage, skillName, data));
        return SkillResult.SUCCESS;
    }
}