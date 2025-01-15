package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

@MythicMechanic(author = "bedwarshurts", name = "minimap", aliases = {}, description = "Controls the minimap state for a player")
public class MinimapMechanic extends SkillMechanic implements INoTargetSkill {

    private final String state;

    public MinimapMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
        this.state = mlc.getString("state", "disable");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        for (AbstractEntity target : data.getEntityTargets()) {
            if (target.isPlayer()) {
                Player player = (Player) target.getBukkitEntity();
                String playerName = player.getName();

                switch (state.toLowerCase()) {
                    case "enable":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minimap enable " + playerName);
                        break;
                    case "disable":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minimap disable " + playerName);
                        break;
                    case "fullscreen":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minimap fullscreen " + playerName);
                        break;
                    default:
                        return SkillResult.CONDITION_FAILED;
                }
                return SkillResult.SUCCESS;
            }
        }
        return SkillResult.CONDITION_FAILED;
    }
}