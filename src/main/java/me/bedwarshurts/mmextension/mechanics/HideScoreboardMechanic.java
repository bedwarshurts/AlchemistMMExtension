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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;

@MythicMechanic(author = "bedwarshurts", name = "hidescoreboard", aliases = {}, description = "Hides the player's scoreboard for a specified time")
public class HideScoreboardMechanic extends SkillMechanic implements INoTargetSkill {

    private final int time;

    public HideScoreboardMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
        this.time = mlc.getInteger("time", 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        for (AbstractEntity target : data.getEntityTargets()) {
            if (target.isPlayer()) {
                Player player = (Player) target.getBukkitEntity();
                String playerName = player.getName();
                String hideCommand = String.format("tab bossbar off %s -s", playerName);
                String showCommand = String.format("tab bossbar on %s -s", playerName);

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), hideCommand);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), showCommand);
                    }
                }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("AlchemistMMExtension")), time);

                return SkillResult.SUCCESS;
            }
        }
        return SkillResult.CONDITION_FAILED;
    }
}