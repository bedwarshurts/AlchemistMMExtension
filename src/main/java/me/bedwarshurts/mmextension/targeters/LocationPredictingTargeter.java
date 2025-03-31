package me.bedwarshurts.mmextension.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;

@MythicTargeter(author = "bedwarshurts", name = "targetpredictedlocation", aliases = {"TPL"}, description = "Predicts the location of the target")
public class LocationPredictingTargeter implements ILocationTargeter {

    private final PlaceholderDouble predictionTimeTicks;
    private final double yOffset;
    private final boolean ignoreY;
    private final boolean ignoreIfStill;

    public LocationPredictingTargeter(MythicLineConfig mlc) {
        MythicBukkit.inst().getPlayerManager().trackPlayerMovement();

        this.predictionTimeTicks = PlaceholderDouble.of(mlc.getString("time", String.valueOf(1.0)));
        this.yOffset = mlc.getDouble("y", 0.0);
        this.ignoreY = mlc.getBoolean(new String[]{"ignoreY", "iy"}, false);
        this.ignoreIfStill = mlc.getBoolean(new String[]{"ignoreStill", "is"}, false);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata data) {
        Set<AbstractLocation> locations = new HashSet<>();

        for (AbstractEntity targetEntity : data.getEntityTargets()) {
            if (!targetEntity.isPlayer()) continue;

            Entity bukkitEntity = targetEntity.getBukkitEntity();
            UUID entityId = bukkitEntity.getUniqueId();
            PlayerManager.PlayerMovementData playerMovementData = MythicBukkit.inst().getPlayerManager().getPlayerPositions().get(entityId);
            Location currentLocation = playerMovementData.getTo();
            Location previousLocation = playerMovementData.getFrom();

            Vector direction = currentLocation.toVector().subtract(previousLocation.toVector()).normalize();

            if (ignoreY) {
                direction.setY(0);
            }

            double speedBpt = 4.317; // Default speed
            if (bukkitEntity instanceof Player player) {
                speedBpt = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue();
            }

            Vector predictedMovement = direction.multiply(speedBpt * predictionTimeTicks.get(data));

            boolean isStill = Double.isNaN(direction.length()) || System.currentTimeMillis() - playerMovementData.getLastMovementTime() >= 60;
            if (isStill && ignoreIfStill) continue;

            Location targetLocation = currentLocation.clone();
            if (!isStill) {
                targetLocation.add(predictedMovement);
            }
            targetLocation.setY(targetLocation.getY() + yOffset);

            locations.add(new AbstractLocation(Position.of(targetLocation)));
        }

        return locations;
    }
}
