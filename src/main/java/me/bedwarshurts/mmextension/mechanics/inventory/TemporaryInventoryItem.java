package me.bedwarshurts.mmextension.mechanics.inventory;

import org.bukkit.inventory.ItemStack;

public class TemporaryInventoryItem {
    private final ItemStack item;
    private String skill;

    public TemporaryInventoryItem (ItemStack item) {
        this.item = item;
        this.skill = null;
    }

    public TemporaryInventoryItem (ItemStack item, String skill) {
        this.item = item;
        this.skill = skill;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public ItemStack getItem() {
        return item;
    }
}
