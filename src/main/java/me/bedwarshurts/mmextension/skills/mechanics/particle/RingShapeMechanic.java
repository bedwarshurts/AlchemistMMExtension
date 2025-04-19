package me.bedwarshurts.mmextension.skills.mechanics.particle;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "ringshape", aliases = {}, description = "Spawns particles in a ring shape and casts a skill at each particle location")
public class RingShapeMechanic extends ParticleMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
    private final List<PlaceholderDouble> rotation;
    private final List<PlaceholderDouble> rotMultiplier;
    private final PlaceholderInt density;
    private final boolean matchRotation;

    public RingShapeMechanic(MythicLineConfig mlc) {
        super(mlc);
        String[] rotationArgs = mlc.getString(new String[]{"rotation", "rot"}, "0,0,0").split(",");
        this.rotation = List.of(
                PlaceholderDouble.of(rotationArgs[0]),
                PlaceholderDouble.of(rotationArgs[1]),
                PlaceholderDouble.of(rotationArgs[2])
        );
        String[] rotMultiplierArgs = mlc.getString(new String[]{"rotMultiplier","rotMult"}, "0,0,0").split(",");
        this.rotMultiplier = List.of(
                PlaceholderDouble.of(rotMultiplierArgs[0]),
                PlaceholderDouble.of(rotMultiplierArgs[1]),
                PlaceholderDouble.of(rotMultiplierArgs[2])
        );
        this.density = PlaceholderInt.of(mlc.getString(new String[]{"density","d"}, "1"));
        this.matchRotation = mlc.getBoolean(new String[]{"matchRotation", "mp"}, false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        return this.castAtLocation(data, target.getLocation());
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation targetLocation) {
        Location origin = targetLocation.toPosition().toLocation();
        Random random = new Random();
        double currentRadius = radius.get(data);
        List<Double> currentDirection = direction.stream().map(d -> d.get(data)).collect(Collectors.toList());
        List<Double> currentRotation = rotation.stream().map(r -> Math.toRadians(r.get(data))).collect(Collectors.toList());
        List<Double> currentRotMultiplier = rotMultiplier.stream().map(r -> Math.toRadians(r.get(data))).toList();
        final int densityValue = density.get(data);

        final Set<Player> audience = SkillUtils.getAudienceTargets(data, this.audience);

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

        Vector lookDirection;
        if (matchRotation) {
            lookDirection = data.getCaster().getEntity().getBukkitEntity().getLocation().getDirection();
            // Apply rotation as an offset if match rotation is true
            SkillUtils.rotateVector(lookDirection, currentRotation.get(0), currentRotation.get(1), currentRotation.get(2));
        } else {
            lookDirection = null;
        }

        for (int i = 0; i < particleCount.get(data); i++) {
            currentRadius += shiftRadius.get(data);
            currentDirection.replaceAll(aDouble -> aDouble * dirMultiplier.get(data));
            currentRotation.set(0, currentRotation.get(0) + currentRotMultiplier.get(0));
            currentRotation.set(1, currentRotation.get(1) + currentRotMultiplier.get(1));
            currentRotation.set(2, currentRotation.get(2) + currentRotMultiplier.get(2));
            double dr = currentRadius + (random.nextDouble() * 2 - 1) * variance.get(data);
            List<Double> updatedRotation = List.copyOf(currentRotation);
            Bukkit.getScheduler().runTaskLaterAsynchronously(AlchemistMMExtension.inst(), () -> {
                for (int j = 0; j < densityValue; j++) {
                    double angle = 2 * Math.PI * random.nextDouble();
                    double x = dr * Math.cos(angle);
                    double z = dr * Math.sin(angle);

                    Vector particleVector = new Vector(x, 0, z);
                    SkillUtils.rotateVector(particleVector, updatedRotation.get(0), updatedRotation.get(1), updatedRotation.get(2));

                    if (lookDirection != null) {
                        SkillUtils.rotateVectorToDirection(particleVector, lookDirection);
                    }

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

                    skill.castAtPoint(data, particleLocation);
                }
            }, (long) (delayMs.get(data) * i / 50));
        }
        return SkillResult.SUCCESS;
    }
}
