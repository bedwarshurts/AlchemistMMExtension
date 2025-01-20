package me.bedwarshurts.mmextension.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

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
}