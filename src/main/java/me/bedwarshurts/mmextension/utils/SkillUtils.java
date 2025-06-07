package me.bedwarshurts.mmextension.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.audience.TargeterAudience;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SkillUtils {

    private SkillUtils() {
        throw new UnsupportedOperationException("You really shouldnt initialise this class");
    }

    public static void openChest(Block block, boolean open, List<Player> players) {
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

    public static void spawnParticle(Set<Player> audience, Particle particleType, Location particleLocation, double dx, double dy, double dz, double speed) {
        spawnParticle(audience, particleType, particleLocation, dx, dy, dz, speed, 0);
    }

    public static void spawnParticle(Set<Player> audience, Particle particleType, Location particleLocation, double dx, double dy, double dz, double speed, int count) {
        if (Bukkit.getWorld(particleLocation.getWorld().getKey()) == null) return;

        if (audience == null) {
            particleLocation.getWorld().spawnParticle(particleType, particleLocation, 0, dx, dy, dz, speed);
            return;
        }
        for (Player player : audience) {
            player.spawnParticle(particleType, particleLocation, count, dx, dy, dz, speed, null, true);
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
        if (audienceTargeter == null) return null;

        return audienceTargeter.get(data, data.getCaster().getEntity()).stream()
                .filter(Objects::nonNull).map(AbstractEntity::getBukkitEntity)
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .collect(Collectors.toSet());
    }

    public static void rotateVectorToDirection(Vector vector, Vector direction) {
        direction = direction.clone().normalize();
        double yaw = Math.atan2(direction.getZ(), direction.getX());
        double pitch = Math.asin(direction.getY());

        // Default rotation of 90 degrees on the Z-axis
        double cosZ = Math.cos(Math.toRadians(90));
        double sinZ = Math.sin(Math.toRadians(90));
        double x = vector.getX() * cosZ - vector.getY() * sinZ;
        double y = vector.getX() * sinZ + vector.getY() * cosZ;
        vector.setX(x).setY(y);

        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);

        // Rotate around yaw (Y axis)
        x = vector.getX() * cosYaw - vector.getZ() * sinYaw;
        double z = vector.getX() * sinYaw + vector.getZ() * cosYaw;
        vector.setX(x).setZ(z);

        // Rotate around pitch (X axis)
        y = vector.getY() * cosPitch - vector.getZ() * sinPitch;
        z = vector.getY() * sinPitch + vector.getZ() * cosPitch;
        vector.setY(y).setZ(z);

    }

    public static PlayerData getMythicPlayer(Player player) {
        Optional<PlayerData> optionalMythicPlayer = MythicBukkit.inst().getPlayerManager().getProfile(player.getUniqueId());

        return optionalMythicPlayer.orElse(null);

    }

    public static void castItemSkill(PlayerInteractEvent event, NamespacedKey skillKey, NamespacedKey casterKey) {
        if (event.getItem() == null) return;
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;

        if (!(meta.getPersistentDataContainer().has(skillKey, PersistentDataType.STRING) && meta.getPersistentDataContainer().has(casterKey, PersistentDataType.STRING)))
            return;

        String skillName = meta.getPersistentDataContainer().get(skillKey, PersistentDataType.STRING);
        String casterUUID = meta.getPersistentDataContainer().get(casterKey, PersistentDataType.STRING);
        SkillCaster caster = new GenericCaster(BukkitAdapter.adapt(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(casterUUID)))));
        SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, caster, BukkitAdapter.adapt(event.getPlayer()));

        MythicSkill skill = new MythicSkill(skillName);
        skill.cast(data);
    }
}