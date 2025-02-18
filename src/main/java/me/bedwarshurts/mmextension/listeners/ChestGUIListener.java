package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.bedwarshurts.mmextension.mechanics.ChestGUIMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class ChestGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!ChestGUIMechanic.INVENTORY_ACTIONS.containsKey(inv)) return;

        SkillMetadata data = ChestGUIMechanic.INVENTORY_METADATA.get(inv);

        if (event.getRawSlot() < 0 || event.getCurrentItem() == null) return;

        Map<Integer, Map<String, String>> actionsMap = ChestGUIMechanic.INVENTORY_ACTIONS.get(inv);
        if (!actionsMap.containsKey(event.getRawSlot())) return;
        Map<String, String> actions = actionsMap.get(event.getRawSlot());

        if ("false".equalsIgnoreCase(actions.getOrDefault("interact", "true"))) {
            event.setCancelled(true);
        }

        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        boolean rightClick = (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT);
        boolean leftClick = (clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT);

        if (rightClick && actions.containsKey("right_click_action")) {
            runAction(player, data, actions.get("right_click_action"));
        }
        if (leftClick && actions.containsKey("left_click_action")) {
            runAction(player, data, actions.get("left_click_action"));
        }
    }

    private void runAction(Player player, SkillMetadata data, String action) {
        // action example: "command:some_command" or "console_command:some_console_command"
        String[] parts = action.split(":", 2);
        if (parts.length < 2) return;

        String type = parts[0];
        String execute = parts[1];

        if ("command".equalsIgnoreCase(type)) {
            player.performCommand(execute);
        } else if ("console_command".equalsIgnoreCase(type)) {
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, execute);
        }
        else if ("skill".equalsIgnoreCase(type)) {
            SkillUtils.castSkill(MythicBukkit.inst().getSkillManager(), data, execute);
        } else {
            player.sendMessage(NamedTextColor.GRAY + "[SYS] " + NamedTextColor.WHITE + "Invalid action, please contact the Alchemist admin team if you see this " + type);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        ChestGUIMechanic.INVENTORY_ACTIONS.remove(inv);
        ChestGUIMechanic.INVENTORY_METADATA.remove(inv);
    }
}