package me.bedwarshurts.mmextension.commands.subcommands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import me.bedwarshurts.mmextension.commands.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /ame playerspawnmythicmob <player> <x> <y> <z> <world> <mobName>
 */
public class PlayerSpawnMythicMobCommand extends AbstractCommand {

    public PlayerSpawnMythicMobCommand() {
        super("playerspawnmythicmob", "alchemist.spawnmythicmob", "<player> <x> <y> <z> <world> <mobName>");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 6) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("[SYS] Player not found.").color(NamedTextColor.RED));
            return true;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("[SYS] Coordinates must be numbers.").color(NamedTextColor.RED));
            return true;
        }

        World world = Bukkit.getWorld(args[4]);
        if (world == null) {
            sender.sendMessage(Component.text("[SYS] World not found.").color(NamedTextColor.RED));
            return true;
        }

        String mobName = args[5];
        Location spawnLocation = new Location(world, x, y, z);

        ActiveMob spawnedMob = MythicBukkit.inst().getMobManager().spawnMob(mobName, spawnLocation, 1.0);
        VariableRegistry variables = spawnedMob.getVariables();
        variables.put("target", new StringVariable(target.getName()));

        sender.sendMessage(Component.text("Mob spawned successfully.").color(NamedTextColor.GREEN));
        return true;
    }
}