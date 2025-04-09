package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.audience.TargeterAudience;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import me.bedwarshurts.mmextension.utils.exceptions.DependencyNotFoundException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

@MythicMechanic(author = "bedwarshurts", name = "openchest", aliases = {}, description = "Opens a chest by mimicking a player opening it")
public class OpenChestMechanic implements ITargetedLocationSkill {
    private final String action;
    private final TargeterAudience audience;

    public OpenChestMechanic(MythicLineConfig mlc) {
        if (!AlchemistMMExtension.inst().isProtocolLib()) throw new DependencyNotFoundException("ProtocolLIB is required to use OpenChestMechanic");

        String audienceTargeterString = mlc.getString("audience", null);
        this.audience = audienceTargeterString != null ? new TargeterAudience(mlc, audienceTargeterString) : null;
        this.action = mlc.getString("action", "open");
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
        Location location = BukkitAdapter.adapt(target);
        Block block = location.getBlock();

        if (block.getType() == Material.CHEST) {
            List<Player> players = SkillUtils.getAudienceTargets(data, audience).stream().toList();

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
                    AlchemistMMExtension.inst().getLogger().warning("Invalid action for openchest mechanic: " + action + " (valid actions: open, close)");
                    yield SkillResult.INVALID_CONFIG;
                }
            };
        }
        return SkillResult.CONDITION_FAILED;
    }
}