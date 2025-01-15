package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SkillUtils {

    public static Optional<Player> getPlayerEntity(SkillMetadata data) {
        for (AbstractEntity target : data.getEntityTargets()) {
            if (target.isPlayer()) {
                return Optional.of((Player) target.getBukkitEntity());
            }
        }
        return Optional.empty();
    }
}
