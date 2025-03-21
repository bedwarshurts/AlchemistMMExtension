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

    private final PlaceholderDouble predictionTimeSeconds;
    private final double yOffset;
    private final boolean ignoreY;
    private final boolean ignoreIfStill;
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public LocationPredictingTargeter(MythicLineConfig mlc) {
        this.predictionTimeSeconds = PlaceholderDouble.of(mlc.getString("time", String.valueOf(1.0)));
        this.yOffset = mlc.getDouble("y", 0.0);
        this.ignoreY = mlc.getBoolean(new String[]{"ignoreY","iy"}, false);
        this.ignoreIfStill = mlc.getBoolean(new String[]{"ignoreStill","is"}, false);
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

            // Always update the previous location
            previousLocations.put(entityId, currentLocation);

            if (ignoreY) {
                direction.setY(0);
            }

            double speedBps = 4.317; // Default speed
            if (bukkitEntity instanceof Player player) {
                speedBps = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue() * 20; // Convert to blocks per second
            }

            Location targetLocation;
            if (Double.isNaN(direction.length())) { // direction.length() returns NaN if the player isn't moving
                if (ignoreIfStill) {
                    continue;
                } else {
                    targetLocation = currentLocation.clone();
                }
            } else {
                Vector predictedMovement = direction.multiply(speedBps * predictionTimeSeconds.get(data));
                targetLocation = currentLocation.clone().add(predictedMovement);
                targetLocation.setY(targetLocation.getY() + yOffset);
            }

            Position position = Position.of(targetLocation);
            locations.add(new AbstractLocation(position));

            previousLocations.put(entityId, currentLocation);
        }

        return locations;
    }
}
