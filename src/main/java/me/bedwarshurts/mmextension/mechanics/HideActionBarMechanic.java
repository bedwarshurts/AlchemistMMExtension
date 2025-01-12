package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

@MythicMechanic(author = "bedwarshurts", name = "hideactionbar", aliases = {}, description = "Hides the player's action bar for a specified time")
public class HideActionBarMechanic extends SkillMechanic implements INoTargetSkill {

    private final int time;

    public HideActionBarMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
        this.time = mlc.getInteger("time", 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        for (AbstractEntity target : data.getEntityTargets()) {
            if (target.isPlayer()) {
                Player player = (Player) target.getBukkitEntity();
                String command = String.format("rpg admin hideab %s %d", player.getName(), time);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                return SkillResult.SUCCESS;
            }
        }
        return SkillResult.CONDITION_FAILED;
    }
}