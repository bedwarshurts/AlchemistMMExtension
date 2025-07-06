package me.bedwarshurts.mmextension.skills.mechanics.chestgui;

import io.lumine.mythic.api.skills.SkillMetadata;
import lombok.Getter;
import lombok.Setter;
import me.bedwarshurts.mmextension.utils.ItemUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
public class ChestGUIHolder implements InventoryHolder {
    @Getter private final SkillMetadata metadata;
    @Getter private final ChestGUISlot[] slots;
    @Setter private Inventory inventory;

    protected ChestGUIHolder(SkillMetadata metadata, int size) {
        this.metadata = metadata;
        this.slots = new ChestGUISlot[size];
    }

    protected ChestItem addItemFromTemplate(String template) {
        ItemStack stack = ItemUtils.buildItem(template);

        String bracket = template.substring(template.indexOf('[') + 1, template.lastIndexOf(']'));
        Map<String, String> info = ItemUtils.parse(bracket);
        int slot = Integer.parseInt(info.getOrDefault("slot", "0"));
        if (slot < 0 || slot >= slots.length) return null;

        ChestGUISlot guiSlot = new ChestGUISlot(slot, stack, info);
        slots[slot] = guiSlot;
        return new ChestItem(slot, stack);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}