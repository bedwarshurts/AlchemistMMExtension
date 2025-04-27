package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@MythicMechanic(author = "bedwarshurts", name = "controlmovement", aliases = {}, description = "Controls the movement of a player to a specified location")
public class ControlMovementMechanic implements ITargetedLocationSkill {

    private final double speed;

    public ControlMovementMechanic(MythicLineConfig mlc) {
        this.speed = mlc.getDouble("speed", 1.0);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
        if (!data.getCaster().getEntity().isPlayer()) return SkillResult.CONDITION_FAILED;

        Player player = (Player) data.getCaster().getEntity().getBukkitEntity();
        rideToLocation(player, BukkitAdapter.adapt(target), speed);
        return SkillResult.SUCCESS;
    }

    private void rideToLocation(Player player, Location destination, double speed) {
        Location start = player.getLocation();

        ArmorStand stand = player.getWorld().spawn(start, ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setInvulnerable(true);

        stand.addPassenger(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                Location current = stand.getLocation();
                if (current.distance(destination) < speed) {
                    stand.teleport(destination);
                    stand.remove();
                    cancel();
                    return;
                }

                Vector direction = destination.toVector().subtract(current.toVector()).normalize().multiply(speed);
                Location next = current.add(direction);
                stand.teleport(next);
            }
        }.runTaskTimer(AlchemistMMExtension.inst(), 0L, 1L);
    }
}
