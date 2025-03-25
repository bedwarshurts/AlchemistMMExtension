package me.bedwarshurts.mmextension.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;

@MythicTargeter(author = "bedwarshurts", name = "targetpredictedlocation", aliases = {"TPL"}, description = "Predicts the location of the target")
public final class LocationPredictingTargeter implements ILocationTargeter {

    private final PlaceholderDouble predictionTimeTicks;
    private final double yOffset;
    private final boolean ignoreY;
    private final boolean ignoreIfStill;
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public LocationPredictingTargeter(MythicLineConfig mlc) {
        this.predictionTimeTicks = PlaceholderDouble.of(mlc.getString("time", String.valueOf(1.0)));
        this.yOffset = mlc.getDouble("y", 0.0);
        this.ignoreY = mlc.getBoolean(new String[]{"ignoreY", "iy"}, false);
        this.ignoreIfStill = mlc.getBoolean(new String[]{"ignoreStill", "is"}, false);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata data) {
        Set<AbstractLocation> locations = new HashSet<>();

        for (AbstractEntity targetEntity : data.getEntityTargets()) {
            Entity bukkitEntity = targetEntity.getBukkitEntity();
            UUID entityId = bukkitEntity.getUniqueId();
            Location currentLocation = bukkitEntity.getLocation();
            Location previousLocation = previousLocations.getOrDefault(entityId, currentLocation);

            Vector direction = currentLocation.toVector().subtract(previousLocation.toVector()).normalize();

            previousLocations.put(entityId, currentLocation);

            if (ignoreY) {
                direction.setY(0);
            }

            double speedBpt = 4.317; // Default speed
            if (bukkitEntity instanceof Player player) {
                speedBpt = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue();
            }

            if (Double.isNaN(direction.length())) { // direction.length() returns NaN if the player isn't moving
                if (ignoreIfStill) {
                    continue;
                }
            }

            Vector predictedMovement = direction.multiply(speedBpt * predictionTimeTicks.get(data));
            Location targetLocation = currentLocation.clone().add(predictedMovement);
            targetLocation.setY(targetLocation.getY() + yOffset);

            locations.add(new AbstractLocation(Position.of(targetLocation)));
        }

        return locations;
    }
}
