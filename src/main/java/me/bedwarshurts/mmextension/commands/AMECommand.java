package me.bedwarshurts.mmextension.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class AMECommand implements CommandExecutor {
    private static final Map<String, AbstractCommand> subcommands = new HashMap<>();

    public static void registerSubcommand(AbstractCommand cmd) {
        subcommands.put(cmd.getName().toLowerCase(), cmd);
    }

    public static void unregisterSubcommand(AbstractCommand cmd) {
        subcommands.remove(cmd.getName().toLowerCase());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /ame <subcommand> [args]");
            return true;
        }

        String sub = args[0].toLowerCase();
        AbstractCommand cmd = subcommands.get(sub);
        if (cmd == null) {
            sender.sendMessage("Unknown subcommand '" + sub + "'.");
            return true;
        }

        if (cmd.getPermission() != null && !sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        boolean result = cmd.execute(sender, subArgs);
        if (!result) {
            sender.sendMessage("Usage: /ame " + cmd.getName() + " " + cmd.getUsage());
        }
        return true;
    }
}
