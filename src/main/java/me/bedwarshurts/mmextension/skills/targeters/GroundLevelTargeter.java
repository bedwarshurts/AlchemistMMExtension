package me.bedwarshurts.mmextension.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

@MythicTargeter(author = "bedwarshurts", name = "targetgroundlocation", aliases = {"TGL"}, description = "Targets the block location the target is standing on")
public class GroundLevelTargeter implements ILocationTargeter {

    private final int yOffset;

    public GroundLevelTargeter(MythicLineConfig mlc) {
        this.yOffset = mlc.getInteger("y", 0);
    }

    @Override
    public Set<AbstractLocation> getLocations(SkillMetadata skillMetadata) {
        Set<AbstractLocation> locations = new HashSet<>();

        for (AbstractEntity targetEntity : skillMetadata.getEntityTargets()) {
            Entity bukkitEntity = targetEntity.getBukkitEntity();
            Location location = bukkitEntity.getLocation();

            // Find the first non-air block below the location
            while (location.getBlock().isEmpty() && location.getY() > 0) {
                location.subtract(0, 1, 0);
            }

            Location blockLocation = location.getBlock().getLocation().add(0, yOffset, 0);
            Position position = Position.of(blockLocation);
            locations.add(new AbstractLocation(position));
        }

        return locations;
    }
}
