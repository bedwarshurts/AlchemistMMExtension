package me.bedwarshurts.mmextension.skills.mechanics.chestgui;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record ChestGUISlot(int slot, ItemStack item, Map<String, String> actions) {

    public boolean canInteract() {
        return !actions.getOrDefault("interact", "false").equalsIgnoreCase("false");
    }

    public String getRightClickAction() {
        return actions.get("right_click_action");
    }

    public String getLeftClickAction() {
        return actions.get("left_click_action");
    }
}