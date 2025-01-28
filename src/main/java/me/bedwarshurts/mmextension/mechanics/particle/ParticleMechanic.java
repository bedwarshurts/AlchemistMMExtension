package me.bedwarshurts.mmextension.mechanics.particle;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.audience.TargeterAudience;
import org.bukkit.Particle;

import java.util.List;

public abstract class ParticleMechanic {
    protected final Particle particleType;
    protected final PlaceholderInt particleCount;
    protected final PlaceholderDouble radius;
    protected final PlaceholderDouble dirMultiplier;
    protected final PlaceholderDouble variance;
    protected final PlaceholderDouble shiftRadius;
    protected final List<PlaceholderDouble> direction;
    protected final PlaceholderInt speed;
    protected final PlaceholderString skillName;
    protected final SkillExecutor skillExecutor;
    protected final PlaceholderDouble delay;
    protected final TargeterAudience audienceTargeter;

    public ParticleMechanic(SkillExecutor manager, MythicLineConfig mlc) {
        this.particleType = Particle.valueOf(mlc.getString("particle", "FLAME").toUpperCase());
        this.radius = PlaceholderDouble.of(mlc.getString("radius", "1.0"));
        this.particleCount = PlaceholderInt.of(mlc.getString("count", "100"));
        this.dirMultiplier = PlaceholderDouble.of(mlc.getString("dirMultiplier", "1.0"));
        this.shiftRadius = PlaceholderDouble.of(mlc.getString("shift", "0.0"));
        this.variance = PlaceholderDouble.of(mlc.getString("variance", "0.0"));
        String[] directionArgs = mlc.getString("direction", "0,0,0").split(",");
        this.speed = PlaceholderInt.of(mlc.getString("speed", "0.1"));
        this.skillName = PlaceholderString.of(mlc.getString("skill", ""));
        this.delay = PlaceholderDouble.of(mlc.getString("delay", "0"));
        this.direction = List.of(
                PlaceholderDouble.of(directionArgs[0]),
                PlaceholderDouble.of(directionArgs[1]),
                PlaceholderDouble.of(directionArgs[2])
        );
        this.skillExecutor = manager;
        String audienceTargeterString = mlc.getString("audience", null);
        this.audienceTargeter = audienceTargeterString != null ? new TargeterAudience(mlc, audienceTargeterString) : null;
    }
}