package me.bedwarshurts.mmextension.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.audience.TargeterAudience;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SkillUtils {

    public static void setChestOpened(Block block, boolean open, List<Player> players) {
        PacketContainer libPacket = new PacketContainer(PacketType.Play.Server.BLOCK_ACTION);
        libPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        libPacket.getIntegers().write(0, 1);
        libPacket.getIntegers().write(1, open ? 1 : 0);
        libPacket.getBlocks().write(0, block.getType());
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        for (Player player : players) {
            manager.sendServerPacket(player, libPacket);
        }
    }

    public static void castSkillAtPoint(SkillMetadata data, Location pointLocation, PlaceholderString skillName, SkillExecutor skillExecutor) {
        if (!skillName.get(data).isEmpty()) { // Execute skill logic
            Optional<Skill> skillOptional = skillExecutor.getSkill(skillName.get(data));
            if (skillOptional.isPresent()) {
                Skill skill = skillOptional.get();
                skill.execute(data.deepClone().setLocationTarget(
                        new AbstractLocation(pointLocation.getWorld().getName(), pointLocation.getX(), pointLocation.getY(), pointLocation.getZ()))
                );
            }
        }
    }

    public static void spawnParticle(Set<Player> audience, Particle particleType, Location particleLocation, double dx, double dy, double dz, double speed ) {
        if (audience != null) {
            for (Player player : audience) {
                player.spawnParticle(particleType, particleLocation, 0, dx, dy, dz, speed);
            }
        } else {
            particleLocation.getWorld().spawnParticle(particleType, particleLocation, 0, dx, dy, dz, speed);
        }
    }

    public static void rotateVector(Vector vector, double xRotation, double yRotation, double zRotation) {
        // Rotate around x-axis
        double y = vector.getY() * Math.cos(xRotation) - vector.getZ() * Math.sin(xRotation);
        double z = vector.getY() * Math.sin(xRotation) + vector.getZ() * Math.cos(xRotation);
        vector.setY(y).setZ(z);

        // Rotate around y-axis
        double x = vector.getX() * Math.cos(yRotation) + vector.getZ() * Math.sin(yRotation);
        z = vector.getZ() * Math.cos(yRotation) - vector.getX() * Math.sin(yRotation);
        vector.setX(x).setZ(z);

        // Rotate around z-axis
        x = vector.getX() * Math.cos(zRotation) - vector.getY() * Math.sin(zRotation);
        y = vector.getX() * Math.sin(zRotation) + vector.getY() * Math.cos(zRotation);
        vector.setX(x).setY(y);
    }

    public static Set<Player> getAudienceTargets(SkillMetadata data, TargeterAudience audienceTargeter) {
        if (audienceTargeter != null) {
            return audienceTargeter.get(data, data.getCaster().getEntity()).stream()
                    .filter(Objects::nonNull)
                    .map(AbstractEntity::getBukkitEntity)
                    .filter(e -> e instanceof Player)
                    .map(e -> (Player) e)
                    .collect(Collectors.toSet());
        }
        return null;
    }
}