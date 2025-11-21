package me.bedwarshurts.mmextension.skills.mechanics.spectatortarget;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.audience.TargeterAudience;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

@MythicMechanic(author = "bedwarshurts", name = "setspectatortarget", description = "Sets the spectator target for a player")
public class SetSpectatorTargetMechanic implements ITargetedEntitySkill {

    private final TargeterAudience audience;
    private final double[] returningPosition;

    public SetSpectatorTargetMechanic(MythicLineConfig mlc) {
        String audienceString = mlc.getString("audience", null);
        this.audience = audienceString != null ? new TargeterAudience(mlc, audienceString) : null;

        String raw = mlc.getString("loc");
        this.returningPosition =
                (raw == null || raw.isBlank())
                        ? null
                        : Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .mapToDouble(Double::parseDouble)
                        .toArray();
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        Set<Player> audienceResults = SkillUtils.getAudienceTargets(data, this.audience);
        if (this.audience == null) {
            return SkillResult.INVALID_CONFIG;
        }
        audienceResults.forEach(player -> new SpectatorData(player, target.getBukkitEntity(),
                returningPosition == null ?
                        new Location(
                                player.getWorld(),
                                player.getLocation().getX(),
                                player.getLocation().getY(),
                                player.getLocation().getZ()
                        ) :
                        new Location(
                                player.getWorld(),
                                returningPosition[0],
                                returningPosition[1],
                                returningPosition[2]
                        )));
        return SkillResult.SUCCESS;
    }
}
