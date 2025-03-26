package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.bedwarshurts.mmextension.mechanics.chestgui.ChestGUIMechanic;
import me.bedwarshurts.mmextension.mechanics.chestgui.ChestGUISlot;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class ChestGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!ChestGUIMechanic.INVENTORY_SLOTS.containsKey(inv)) return;

        SkillMetadata data = ChestGUIMechanic.INVENTORY_METADATA.get(inv);
        if (event.getRawSlot() < 0 || event.getCurrentItem() == null) return;

        ChestGUISlot[] slots = ChestGUIMechanic.INVENTORY_SLOTS.get(inv);
        if (event.getRawSlot() >= slots.length) return;

        ChestGUISlot guiSlot = slots[event.getRawSlot()];
        if (guiSlot == null) return;

        if (!guiSlot.canInteract()) {
            event.setCancelled(true);
        }

        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        boolean rightClick = (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT);
        boolean leftClick = (clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT);

        if (rightClick && guiSlot.getRightClickAction() != null) {
            runAction(player, data, guiSlot.getRightClickAction());
        }
        if (leftClick && guiSlot.getLeftClickAction() != null) {
            runAction(player, data, guiSlot.getLeftClickAction());
        }
    }

    private void runAction(Player player, SkillMetadata data, String action) {
        String[] parts = action.split(":", 2);
        if (parts.length < 2) return;
        String type = parts[0];
        String execute = parts[1];

        if ("command".equalsIgnoreCase(type)) {
            player.performCommand(execute);
        } else if ("console_command".equalsIgnoreCase(type)) {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, execute);
        } else if ("skill".equalsIgnoreCase(type)) {
            data.setTrigger(BukkitAdapter.adapt(player));
            MythicSkill skill = new MythicSkill(execute);
            skill.cast(data);
        } else {
            player.sendMessage(NamedTextColor.GRAY + "[SYS] " + NamedTextColor.WHITE
                    + "Invalid action, please contact the Alchemist admin team if you see this " + type);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();

        ChestGUIMechanic.INVENTORY_SLOTS.remove(inv);
        ChestGUIMechanic.INVENTORY_METADATA.remove(inv);
    }
}