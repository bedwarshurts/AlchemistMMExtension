package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.bedwarshurts.mmextension.skills.mechanics.chestgui.ChestGUIHolder;
import me.bedwarshurts.mmextension.skills.mechanics.chestgui.ChestGUISlot;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof ChestGUIHolder holder)) return;

        event.setCancelled(true);
        int rawSlot = event.getRawSlot();
        ChestGUISlot[] slots = holder.getSlots();
        if (rawSlot < 0 || rawSlot >= slots.length) return;

        ChestGUISlot guiSlot = slots[rawSlot];
        if (guiSlot == null || !guiSlot.canInteract()) return;

        Player player = (Player) event.getWhoClicked();
        boolean right = event.getClick().isRightClick();
        boolean left  = event.getClick().isLeftClick();
        SkillMetadata data = holder.getMetadata();

        if (right && guiSlot.getRightClickAction() != null) {
            runAction(player, data, guiSlot.getRightClickAction());
        }
        if (left && guiSlot.getLeftClickAction() != null) {
            runAction(player, data, guiSlot.getLeftClickAction());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ChestGUIHolder holder) {
            holder.setInventory(null);
        }
    }

    private void runAction(Player player, SkillMetadata data, String action) {
        String[] parts = action.split(":", 2);
        if (parts.length < 2) return;
        String type = parts[0];
        String exec = parts[1];

        switch (type.toLowerCase()) {
            case "command" -> player.performCommand(exec);
            case "console_command" -> {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                Bukkit.dispatchCommand(console, exec);
            }
            case "skill" -> {
                data.setTrigger(BukkitAdapter.adapt(player));
                new MythicSkill(exec).cast(data);
            }
            default -> player.sendMessage(NamedTextColor.GRAY + "[SYS] " + NamedTextColor.WHITE
                    + "Invalid action type: " + type);
        }
    }
}
