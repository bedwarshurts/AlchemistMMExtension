package me.bedwarshurts.mmextension.mechanics.mmocore;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;

@MythicMechanic(author = "bedwarshurts", name = "setmmocooldown", aliases = {"setmmocd"}, description = "Sets the cooldown for a specified mmo ability")
public class SetMMOCooldownMechanic implements INoTargetSkill {
    private final String ability;
    private final double cooldown;

    public SetMMOCooldownMechanic(MythicLineConfig config) {
        this.ability = config.getString(new String[]{"ability", "a"}, "");
        this.cooldown = config.getDouble(new String[]{"cooldown", "cd"}, 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean success = false;

        for (AbstractEntity entity : data.getEntityTargets()) {
            if (entity.getBukkitEntity() instanceof Player player) {
                PlayerData playerData = PlayerData.get(player);
                try {
                    playerData.getCooldownMap().resetCooldown(playerData.getProfess().getSkill(MMOCore.plugin.skillManager.getSkill(ability)));
                    playerData.getCooldownMap().applyCooldown(playerData.getProfess().getSkill(MMOCore.plugin.skillManager.getSkill(ability)), cooldown);
                }
                catch (Exception e) {
                    AlchemistMMExtension.AlchemistMMExtension.getLogger().info("Failed to apply cooldown for " + ability + " to " + player.getName());
                    throw e;
                }
                success = true;
            }
        }

        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}