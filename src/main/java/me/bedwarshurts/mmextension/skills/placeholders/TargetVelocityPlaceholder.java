package me.bedwarshurts.mmextension.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.skills.placeholders.types.EntityPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@MythicPlaceholder(placeholder = "target.vel", description = "Returns the true velocity of the target.")
public class TargetVelocityPlaceholder implements EntityPlaceholder {

    @Override
    public String apply(AbstractEntity target, String s) {
        if (!AlchemistMMExtension.inst().isTrackingPlayerMovement()) {
            return "[This placeholder requires player movement tracking to be enabled]";
        }

        PlayerManager.PlayerMovementData movementData = MythicBukkit.inst()
                .getPlayerManager()
                .getPlayerPositions()
                .get(target.getUniqueId());
        if (movementData == null) {
            return "0";
        }

        Location from = movementData.getFrom();
        Location to = movementData.getTo();
        Vector delta = to.toVector().subtract(from.toVector());

        double seconds = (System.currentTimeMillis() - movementData.getLastMovementTime()) / 1000.0;
        if (seconds <= 0 || Double.isNaN(delta.length()) || seconds > 0.06) {
            return "0";
        }

        Vector velocity = delta.clone().multiply(20);

        return switch (s) {
            case "x" -> String.valueOf(velocity.getX());
            case "y" -> String.valueOf(velocity.getY());
            case "z" -> String.valueOf(velocity.getZ());
            case "length" -> String.valueOf(velocity.length());
            case "all" -> "x: " + velocity.getX() +
                    ", y: " + velocity.getY() +
                    ", z: " + velocity.getZ();
            default -> "[Invalid placeholder]";
        };
    }
}