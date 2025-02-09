package me.bedwarshurts.mmextension.mechanics.particle;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "ringshape", aliases = {}, description = "Spawns particles in a ring shape and casts a skill at each particle location")
public class RingShapeMechanic extends ParticleMechanic implements ITargetedLocationSkill {
    private final List<PlaceholderDouble> rotation;
    private final List<PlaceholderDouble> rotMultiplier;
    private final PlaceholderInt density;
    private final boolean matchRotation;

    public RingShapeMechanic(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        String[] rotationArgs = mlc.getString("rotation", "0,0,0").split(",");
        this.rotation = List.of(
                PlaceholderDouble.of(rotationArgs[0]),
                PlaceholderDouble.of(rotationArgs[1]),
                PlaceholderDouble.of(rotationArgs[2])
        );
        String[] rotMultiplierArgs = mlc.getString("rotMultiplier", "0,0,0").split(",");
        this.rotMultiplier = List.of(
                PlaceholderDouble.of(rotMultiplierArgs[0]),
                PlaceholderDouble.of(rotMultiplierArgs[1]),
                PlaceholderDouble.of(rotMultiplierArgs[2])
        );
        this.density = PlaceholderInt.of(mlc.getString("density", "1"));
        this.matchRotation = mlc.getBoolean("matchRotation", false);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation targetLocation) {
        Location origin = targetLocation.toPosition().toLocation();
        Random random = new Random();

        double currentRadius = radius.get(data);
        List<Double> currentDirection = direction.stream().map(d -> d.get(data)).collect(Collectors.toList());
        List<Double> currentRotation = rotation.stream().map(r -> Math.toRadians(r.get(data))).collect(Collectors.toList());
        List<Double> currentRotMultiplier = rotMultiplier.stream().map(r -> Math.toRadians(r.get(data))).collect(Collectors.toList());
        final int densityValue = density.get(data);

        final Set<Player> audience = SkillUtils.getAudienceTargets(data, audienceTargeter);

        final Vector offset;
        if (dirOverride != null) {
            offset = new Vector(
                    dirOverride.get(0).get(data),
                    dirOverride.get(1).get(data),
                    dirOverride.get(2).get(data)
            ).subtract(origin.toVector());
        } else {
            offset = null;
        }

        if (matchRotation) {
            Location casterLocation = data.getCaster().getEntity().getBukkitEntity().getLocation();
            currentRotation.set(0, Math.toRadians(casterLocation.getPitch()));
            currentRotation.set(1, Math.toRadians(casterLocation.getYaw()));
            currentRotation.set(2, 0.0); // Assuming no roll rotation
        }

        for (int i = 0; i < particleCount.get(data); i++) {
            currentRadius += shiftRadius.get(data);
            for (int k = 0; k < currentDirection.size(); k++) {
                currentDirection.set(k, currentDirection.get(k) * dirMultiplier.get(data));
            }
            currentRotation.set(0, currentRotation.get(0) + currentRotMultiplier.get(0));
            currentRotation.set(1, currentRotation.get(1) + currentRotMultiplier.get(1));
            currentRotation.set(2, currentRotation.get(2) + currentRotMultiplier.get(2));
            double dr = currentRadius + (random.nextDouble() * 2 - 1) * variance.get(data);
            List<Double> updatedRotation = List.copyOf(currentRotation);
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                for (int j = 0; j < densityValue; j++) {
                    double angle = 2 * Math.PI * random.nextDouble();
                    double x = dr * Math.cos(angle);
                    double z = dr * Math.sin(angle);

                    Vector particleVector = new Vector(x, 0, z);
                    SkillUtils.rotateVector(particleVector, updatedRotation.get(0), updatedRotation.get(1), updatedRotation.get(2));

                    Location particleLocation = origin.clone().add(particleVector);

                    Vector endLocation;
                    Vector directionVector;
                    if (offset != null) {
                        endLocation = particleLocation.clone().add(offset).toVector();
                        directionVector = endLocation.subtract(particleLocation.toVector()).normalize();
                    } else {
                        directionVector = particleLocation.toVector().subtract(origin.toVector()).normalize();
                    }

                    directionVector.multiply(new Vector(
                            currentDirection.get(0),
                            currentDirection.get(1),
                            currentDirection.get(2)
                    ));

                    double dx = directionVector.getX() * dirMultiplier.get(data);
                    double dy = directionVector.getY() * dirMultiplier.get(data);
                    double dz = directionVector.getZ() * dirMultiplier.get(data);

                    SkillUtils.spawnParticle(audience, particleType, particleLocation, dx, dy, dz, speed.get(data));

                    SkillUtils.castSkillAtPoint(data, particleLocation, skillName, skillExecutor);
                }
            }, (long) (delayMs.get(data) * i / 50)); // Convert delay from milliseconds to ticks (50 ms = 1 tick)
        }
        return SkillResult.SUCCESS;
    }
}