package me.bedwarshurts.mmextension.commands;

import lombok.Getter;
import me.bedwarshurts.mmextension.utils.terminable.Terminable;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements Terminable {

    @Getter private final String name;
    @Getter private final String permission;
    @Getter private final String usage;

    protected AbstractCommand(String name, String permission, String usage) {
        this.name = name;
        this.permission = permission;
        this.usage = usage;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    @Override
    public void close() {
        AMECommand.unregisterSubcommand(this);
    }
}