package me.bedwarshurts.mmextension.skills.mechanics;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

@MythicMechanic(author = "bedwarshurts", name = "placeblock", aliases = {"pb"}, description = "Send a block packet to the target location")
public class PlaceBlockMechanic implements ITargetedLocationSkill {
    private final String BLOCK_DATA;
    private final boolean fake;

    public PlaceBlockMechanic(MythicLineConfig mlc) {
        this.BLOCK_DATA = mlc.getString(new String[]{"blockData", "data", "block"}, "");
        this.fake = mlc.getBoolean(new String[]{"fake", "f"}, false);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
        Block block = BukkitAdapter.adapt(target).getBlock();
        BlockData blockData = Bukkit.createBlockData(BLOCK_DATA);

        if (fake) {
            PacketContainer packet = getFakeBlockPacket(BukkitAdapter.adapt(target), blockData);
            for (Player player : BukkitAdapter.adapt(target).getNearbyPlayers(30)) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } else {
            block.setType(Bukkit.createBlockData(BLOCK_DATA).getMaterial());
            block.setBlockData(blockData);
        }

        return SkillResult.SUCCESS;
    }

    private PacketContainer getFakeBlockPacket(Location location, BlockData data) {
        WrappedBlockData wrappedBlockData = WrappedBlockData.createData(data);

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0,
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        packet.getBlockData().write(0, wrappedBlockData);

        return packet;
    }
}
