package me.bedwarshurts.mmextension.mechanics.chestgui;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.listeners.ChestGUIListener;
import me.bedwarshurts.mmextension.utils.ItemUtils;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MythicMechanic(author = "bedwarshurts", name = "chestgui", aliases = {}, description = "Opens a custom Chest GUI for target players")
public class ChestGUIMechanic implements INoTargetSkill {

    private final String title;
    private final int slots;
    private final String rawContents;

    public static final Map<Inventory, ChestGUISlot[]> INVENTORY_SLOTS = new HashMap<>();
    public static final Map<Inventory, SkillMetadata> INVENTORY_METADATA = new HashMap<>();

    public ChestGUIMechanic(MythicLineConfig mlc) {
        Bukkit.getPluginManager().registerEvents(new ChestGUIListener(), AlchemistMMExtension.inst());

        this.title = mlc.getString("title", "Alchemist Chest GUI");
        this.slots = Math.max(9, mlc.getInteger("slots", 9));
        this.rawContents = mlc.getString("contents", "");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        String[] items = rawContents.split("],");

        for (AbstractEntity abstractEntity : data.getEntityTargets()) {
            if (!abstractEntity.isPlayer()) continue;
            Player player = (Player) abstractEntity.getBukkitEntity();

            String parsedTitle = PlaceholderAPI.setPlaceholders(player,
                    PlaceholderUtils.parseStringPlaceholders(title, data));
            Inventory inv = Bukkit.createInventory(null, slots, MiniMessage.miniMessage().deserialize(parsedTitle));

            INVENTORY_METADATA.put(inv, data);
            ChestGUISlot[] slotArray = new ChestGUISlot[slots];

            for (String itemString : items) {
                itemString = itemString.trim();
                if (!itemString.contains("[")) continue;

                String itemName = itemString.substring(0, itemString.indexOf('[')).trim().toUpperCase();
                String bracketContent = itemString
                        .substring(itemString.indexOf('[') + 1)
                        .replace("]", "");
                Map<String, String> infoMap = ItemUtils.parse(bracketContent);

                String parsedName = PlaceholderAPI.setPlaceholders(player,
                        PlaceholderUtils.parseStringPlaceholders(infoMap.getOrDefault("name", ""), data));
                String parsedLore = PlaceholderAPI.setPlaceholders(player,
                        PlaceholderUtils.parseStringPlaceholders(infoMap.getOrDefault("lore", ""), data));

                ItemStack stack = ItemUtils.getItemStack(itemName);
                ItemMeta meta = stack.getItemMeta();

                if (!parsedName.isEmpty()) {
                    meta.displayName(MiniMessage.miniMessage().deserialize(parsedName));
                }

                List<Component> loreComponents = new ArrayList<>();
                for (String line : parsedLore.split("\\\\n")) {
                    if (line.isEmpty()) continue;

                    loreComponents.add(MiniMessage.miniMessage().deserialize(line));
                }
                if (!loreComponents.isEmpty()) {
                    meta.lore(loreComponents);
                }
                if (infoMap.get("enchanted").equalsIgnoreCase("true")) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                    meta.setHideTooltip(true);
                }
                stack.setItemMeta(meta);

                int slot = Integer.parseInt(infoMap.getOrDefault("slot", "0"));
                if (slot < 0 || slot >= slots) continue;

                inv.setItem(slot, stack);
                slotArray[slot] = new ChestGUISlot(slot, stack, extractActions(infoMap));
            }
            INVENTORY_SLOTS.put(inv, slotArray);
            player.openInventory(inv);
        }
        return SkillResult.SUCCESS;
    }

    private Map<String, String> extractActions(Map<String, String> infoMap) {
        Map<String, String> actions = new HashMap<>();
        if (infoMap.containsKey("right_click_action")) {
            actions.put("right_click_action", infoMap.get("right_click_action"));
        }
        if (infoMap.containsKey("left_click_action")) {
            actions.put("left_click_action", infoMap.get("left_click_action"));
        }
        if (infoMap.containsKey("interact")) {
            actions.put("interact", infoMap.get("interact"));
        }
        return actions;
    }
}