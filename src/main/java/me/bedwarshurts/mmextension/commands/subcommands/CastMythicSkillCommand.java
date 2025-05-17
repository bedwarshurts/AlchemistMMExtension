package me.bedwarshurts.mmextension.commands.subcommands;

import me.bedwarshurts.mmextension.commands.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * /castmythicskill (caster) (trigger) (skill-name)
 */
public class CastMythicSkillCommand extends AbstractCommand {

    public CastMythicSkillCommand() {
        super("castskill", "alchemist.castskill", "<caster> <trigger> <skill>");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("alchemist.castmythicskill")) return false;

        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /castmythicskill <caster> <trigger> <skill-name>").color(NamedTextColor.RED));
            return false;
        }

        String caster = args[0];
        String trigger = args[1];
        String skillName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));


        return false;
    }
}
