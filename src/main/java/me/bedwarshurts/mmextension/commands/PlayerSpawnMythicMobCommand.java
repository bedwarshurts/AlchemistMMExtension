package me.bedwarshurts.mmextension.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSpawnMythicMobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!sender.hasPermission("alchemist.spawnmythicmob")) {
            sender.sendMessage(
                    Component.text("Unknown ")
                            .color(NamedTextColor.RED)
                            .append(Component.text("command, Type ").color(NamedTextColor.WHITE))
                            .append(Component.text("/help ").color(NamedTextColor.RED))
                            .append(Component.text("for help. ").color(NamedTextColor.WHITE))
                            .append(Component.text("(/").color(NamedTextColor.GRAY))
                            .append(Component.text(command.getName()).color(NamedTextColor.GRAY))
                            .append(Component.text(")").color(NamedTextColor.GRAY)));
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(Component.text("Usage: /playerspawnmythicmob <player> <x> <y> <z> <world> <mobName>").color(NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("[SYS] ").color(NamedTextColor.GRAY)
                    .append(Component.text("Player not found.").color(NamedTextColor.WHITE)));
            return true;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("[SYS] ").color(NamedTextColor.GRAY)
                    .append(Component.text("Coordinates must be numbers.").color(NamedTextColor.WHITE)));
            return true;
        }
        World world = Bukkit.getWorld(args[4]);

        String mobName = args[5];
        Location spawnLocation = new Location(world, x, y, z);

        ActiveMob spawnedMob = MythicBukkit.inst().getMobManager().spawnMob(mobName, spawnLocation, 1.0);
        VariableRegistry variables = spawnedMob.getVariables();
        variables.put("target", new StringVariable(target.getName()));

        return true;
    }
}
