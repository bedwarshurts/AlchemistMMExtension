package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.AlchemistMMExtension;

@MythicMechanic(author = "bedwarshurts", name = "openchest", aliases = {}, description = "Opens a chest by mimicking a player opening it")
public final class OpenChestMechanic implements INoTargetSkill {
    private final String action;

    public OpenChestMechanic(MythicLineConfig mlc) {
        this.action = mlc.getString("action", "open");
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

            return switch (action) {
                case "open" -> {
                    SkillUtils.openChest(block, true, players);
                    yield SkillResult.SUCCESS;
                }
                case "close" -> {
                    SkillUtils.openChest(block, false, players);
                    yield SkillResult.SUCCESS;
                }
                default -> {
                    AlchemistMMExtension.getLogger().warning("Invalid action for openchest mechanic: " + action + " (valid actions: open, close)");
                    yield SkillResult.INVALID_CONFIG;
                }
            };
        }
        return SkillResult.CONDITION_FAILED;
    }
}