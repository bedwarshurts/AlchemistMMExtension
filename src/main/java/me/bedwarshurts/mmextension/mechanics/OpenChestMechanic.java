package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "openchest", aliases = {}, description = "Opens a chest by mimicking a player opening it")
public class OpenChestMechanic extends SkillMechanic implements INoTargetSkill {

    public OpenChestMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        Location location = BukkitAdapter.adapt(data.getCaster().getLocation());
        Block block = location.getBlock();

        if (block.getType() == Material.CHEST) {
            List<Player> players = data.getEntityTargets().stream()
                    .map(BukkitAdapter::adapt)
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .collect(Collectors.toList());
            SkillUtils.setChestOpened(block, true, players);
            return SkillResult.SUCCESS;
        }

        return SkillResult.CONDITION_FAILED;
    }
}