package me.bedwarshurts.mmextension.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
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

@MythicTargeter(author = "bedwarshurts", name = "targetpredictedlocation", aliases = {"TPL"}, description = "Predicts the location of the target")
public class LocationPredictingTargeter implements ILocationTargeter {

    private final double predictionTime;
    private final double yOffset;
    private final boolean ignoreY;
    private final boolean ignoreIfStill;
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public LocationPredictingTargeter(MythicLineConfig mlc) {
        this.predictionTime = mlc.getDouble("time", 1.0); // Read the time parameter from the config, default to 1.0 seconds if not provided
        this.yOffset = mlc.getDouble("y", 0.0); // Read the yOffset parameter from the config, default to 0.0 if not provided
        this.ignoreY = mlc.getBoolean("iy", false); // Read the ignoreY parameter from the config, default to false if not provided
        this.ignoreIfStill = mlc.getBoolean("is", false); // Read the ignoreIfStill parameter from the config, default to false if not provided
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata) {
        Set<AbstractLocation> locations = new HashSet<>();
        AbstractEntity targetEntity = skillMetadata.getCaster().getEntity().getTarget();

        if (targetEntity == null) {
            return locations;
        }

        Entity bukkitEntity = targetEntity.getBukkitEntity();
        UUID entityId = bukkitEntity.getUniqueId();
        Location currentLocation = bukkitEntity.getLocation();
        Location previousLocation = previousLocations.getOrDefault(entityId, currentLocation);

        // Calculate the direction based on the difference between current and previous locations
        Vector direction = currentLocation.toVector().subtract(previousLocation.toVector()).normalize();

        // Ignore Y coordinate if ignoreY is true
        if (ignoreY) {
            direction.setY(0);
        }

        // Get the player's movement speed attribute
        double speed = 4.317; // Default speed
        if (bukkitEntity instanceof Player) {
            Player player = (Player) bukkitEntity;
            speed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 20; // Convert to blocks per second
        }

        Location targetLocation;
        if (Double.isNaN(direction.length())) {
            // If the player isn't moving, check ignoreIfStill
            if (ignoreIfStill) {
                previousLocations.put(entityId, currentLocation);
                return locations; // Return empty locations
            } else {
                targetLocation = currentLocation.clone();
            }
        } else {
            // Predict the future location based on direction, speed, and prediction time
            Vector predictedMovement = direction.multiply(speed * predictionTime);
            targetLocation = currentLocation.clone().add(predictedMovement);
            targetLocation.setY(targetLocation.getY() + yOffset); // Apply the yOffset
        }

        // Use the predicted location directly
        Position position = Position.of(targetLocation);
        locations.add(new AbstractLocation(position));

        // Update the previous location
        previousLocations.put(entityId, currentLocation);

        return locations;
    }
}