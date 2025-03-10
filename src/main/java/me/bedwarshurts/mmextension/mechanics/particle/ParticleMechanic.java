package me.bedwarshurts.mmextension.mechanics.particle;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
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
    protected final PlaceholderDouble speed;
    protected final PlaceholderString skillName;
    protected final PlaceholderDouble delayMs;
    protected final TargeterAudience audienceTargeter;
    protected final List<PlaceholderDouble> dirOverride;

    public ParticleMechanic(MythicLineConfig mlc) {
        this.particleType = Particle.valueOf(mlc.getString(new String[]{"particle", "p"}, "FLAME").toUpperCase());
        this.radius = PlaceholderDouble.of(mlc.getString(new String[]{"radius","r"}, "1.0"));
        this.particleCount = PlaceholderInt.of(mlc.getString(new String[]{"count", "c", "a"}, "100"));
        this.dirMultiplier = PlaceholderDouble.of(mlc.getString(new String[]{"dirMultiplier", "dirMult"}, "1.0"));
        this.shiftRadius = PlaceholderDouble.of(mlc.getString(new String[]{"shiftRadius", "shift"}, "0.0"));
        this.variance = PlaceholderDouble.of(mlc.getString(new String[]{"variance", "v"}, "0.0"));
        this.speed = PlaceholderDouble.of(mlc.getString(new String[]{"speed", "s"}, "1"));
        this.skillName = PlaceholderString.of(mlc.getString(new String[]{"skill", "onPoint", "oP"}, ""));
        this.delayMs = PlaceholderDouble.of(mlc.getString(new String[]{"interval", "i"}, "0"));
        String[] directionArgs = mlc.getString(new String[]{"direction", "dir"}, "0,0,0").split(",");
        this.direction = List.of(
                PlaceholderDouble.of(directionArgs[0]),
                PlaceholderDouble.of(directionArgs[1]),
                PlaceholderDouble.of(directionArgs[2])
        );
        String audienceTargeterString = mlc.getString("audience", null);
        this.audienceTargeter = audienceTargeterString != null ? new TargeterAudience(mlc, audienceTargeterString) : null;
        String[] dirOverrideArgs = mlc.getString(new String[]{"dirOverride", "dirO"}, "null").split(",");
        this.dirOverride = dirOverrideArgs.length == 3 ? List.of(
                PlaceholderDouble.of(dirOverrideArgs[0]),
                PlaceholderDouble.of(dirOverrideArgs[1]),
                PlaceholderDouble.of(dirOverrideArgs[2])
        ) : null;
    }
}
