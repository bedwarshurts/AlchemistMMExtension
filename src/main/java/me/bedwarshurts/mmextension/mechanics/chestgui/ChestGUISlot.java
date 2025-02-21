package me.bedwarshurts.mmextension.mechanics.chestgui;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ChestGUISlot {
    private final int slot;
    private final ItemStack item;
    private final Map<String, String> actions;

    public ChestGUISlot(int slot, ItemStack item, Map<String, String> actions) {
        this.slot = slot;
        this.item = item;
        this.actions = actions;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public Map<String, String> getActions() {
        return actions;
    }

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