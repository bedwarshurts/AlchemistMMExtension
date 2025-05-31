package me.bedwarshurts.mmextension.skills.targeters;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;

@MythicTargeter(author = "bedwarshurts", name = "targetgroundlocation", aliases = {"TGL"}, description = "Targets the block location the target is standing on")
public class GroundLevelTargeter implements ILocationTargeter {

    private final int yOffset;

    public GroundLevelTargeter(MythicLineConfig mlc) {
        this.yOffset = mlc.getInteger("y", 0);
    }

    @Override
    public Set<AbstractLocation> getLocations(SkillMetadata skillMetadata) {
        if (Bukkit.isPrimaryThread()) {
            return computeGroundLocationsSync(skillMetadata);
        }

        Callable<Set<AbstractLocation>> syncTask = () -> computeGroundLocationsSync(skillMetadata);
        Future<Set<AbstractLocation>> future = Bukkit.getScheduler()
                .callSyncMethod(AlchemistMMExtension.inst(), syncTask);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            return new HashSet<>();
        }
    }

    private Set<AbstractLocation> computeGroundLocationsSync(SkillMetadata skillMetadata) {
        Set<AbstractLocation> locations = new HashSet<>();

        for (AbstractEntity targetEntity : skillMetadata.getEntityTargets()) {
            Entity bukkitEntity = targetEntity.getBukkitEntity();
            World world = bukkitEntity.getWorld();
            Location startLoc = bukkitEntity.getLocation();

            int minY = world.getMinHeight();
            int x = startLoc.getBlockX();
            int z = startLoc.getBlockZ();
            int y = startLoc.getBlockY();

            Block groundBlock = null;
            for (int checkY = y; checkY >= minY; checkY--) {
                Block b = world.getBlockAt(x, checkY, z);
                if (b.getType() != Material.AIR && b.getType().isSolid()) {
                    groundBlock = b;
                    break;
                }
            }

            if (groundBlock != null) {
                Location blockLoc = groundBlock.getLocation().add(0, yOffset, 0);

                Position pos = Position.of(blockLoc);
                locations.add(new AbstractLocation(pos));
            } else {
                Block fallback = world.getBlockAt(x, minY, z);
                Location fbLoc = fallback.getLocation().add(0, yOffset, 0);
                Position pos = Position.of(fbLoc);
                locations.add(new AbstractLocation(pos));
            }
        }

        return locations;
    }
}
