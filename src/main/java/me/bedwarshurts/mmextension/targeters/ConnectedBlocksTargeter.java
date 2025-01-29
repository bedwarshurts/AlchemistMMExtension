package me.bedwarshurts.mmextension.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@MythicTargeter(author = "bedwarshurts", name = "targetconnectedblocks", aliases = {"TCB"}, description = "Targets all connected blocks to a location, use with extreme caution")
public class ConnectedBlocksTargeter implements ILocationTargeter {

    private final Set<Material> excludedMaterials;
    private final List<PlaceholderDouble> location;
    private final List<PlaceholderDouble> locationOffset;
    private final int depth;

    public ConnectedBlocksTargeter(MythicLineConfig mlc) {
        String[] excluded = mlc.getString("exclude", "").split(",");
        this.excludedMaterials = new HashSet<>();
        for (String material : excluded) {
            try {
                this.excludedMaterials.add(Material.valueOf(material.toUpperCase()));
            } catch (IllegalArgumentException e) {
                AlchemistMMExtension.AlchemistMMExtension.getLogger().warning("Invalid material: " + material);
            }
        }
        String[] locationArgs = mlc.getString("loc", "").split(",");
        this.location = locationArgs.length == 3 ? List.of(
                PlaceholderDouble.of(locationArgs[0]),
                PlaceholderDouble.of(locationArgs[1]),
                PlaceholderDouble.of(locationArgs[2])
        ) : null;

        String[] offsetArgs = mlc.getString("locOffset", "0,0,0").split(",");
        this.locationOffset = List.of(
                PlaceholderDouble.of(offsetArgs[0]),
                PlaceholderDouble.of(offsetArgs[1]),
                PlaceholderDouble.of(offsetArgs[2])
        );

        this.depth = mlc.getInteger("depth", 10);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata data) {
        Set<AbstractLocation> locations = new HashSet<>();
        Set<Location> visited = new HashSet<>();
        Queue<Location> queue = new LinkedList<>();
        World world = BukkitAdapter.adapt(data.getCaster().getLocation().getWorld());

        Location startLocation;
        if (location != null) {
            startLocation = new Location(world, location.get(0).get(data), location.get(1).get(data), location.get(2).get(data));
        } else {
            startLocation = BukkitAdapter.adapt(data.getCaster().getLocation());
        }

        startLocation.add(locationOffset.get(0).get(data), locationOffset.get(1).get(data), locationOffset.get(2).get(data));
        queue.add(startLocation);

        int blocksDetected = 0;
        while (!queue.isEmpty() && blocksDetected < depth) {
            Location current = queue.poll();
            if (!visited.add(current)) {
                continue;
            }
            Block block = current.getBlock();
            if (block.getType() == Material.AIR || excludedMaterials.contains(block.getType())) {
                continue;
            }
            locations.add(new AbstractLocation(Position.of(current)));
            blocksDetected++;

            Location[] neighbors = {
                    current.clone().add(1, 0, 0),
                    current.clone().add(-1, 0, 0),
                    current.clone().add(0, 1, 0),
                    current.clone().add(0, -1, 0),
                    current.clone().add(0, 0, 1),
                    current.clone().add(0, 0, -1)
            };
            for (Location neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        return locations;
    }
}