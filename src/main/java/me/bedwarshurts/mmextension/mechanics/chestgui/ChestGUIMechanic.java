package me.bedwarshurts.mmextension.mechanics.chestgui;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MythicMechanic(author = "bedwarshurts", name = "chestgui", aliases = {}, description = "Opens a custom Chest GUI for target players")
public class ChestGUIMechanic implements INoTargetSkill {

    private final String title;
    private final int slots;
    private final String rawContents;

    public static final Map<Inventory, ChestGUISlot[]> INVENTORY_SLOTS = new WeakHashMap<>();
    public static final Map<Inventory, SkillMetadata> INVENTORY_METADATA = new WeakHashMap<>();

    public ChestGUIMechanic(MythicLineConfig mlc) {
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
                Map<String, String> infoMap = parseBracketContent(bracketContent);

                String parsedName = PlaceholderAPI.setPlaceholders(player,
                        PlaceholderUtils.parseStringPlaceholders(infoMap.getOrDefault("name", ""), data));
                String parsedLore = PlaceholderAPI.setPlaceholders(player,
                        PlaceholderUtils.parseStringPlaceholders(infoMap.getOrDefault("lore", ""), data));

                ItemStack stack = getItemFromConfig(itemName);
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

    private Map<String, String> parseBracketContent(String content) {
        Map<String, String> map = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)=([^,\\]]*)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim().replaceAll("^\"|\"$", "");
            map.put(key.toLowerCase(), value);
        }
        return map;
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

    private ItemStack getItemFromConfig(String itemString) {
        if (itemString.toLowerCase().startsWith("mmoitem:")) {
            String[] parts = itemString.split(":");
            if (parts.length == 3) {
                MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(parts[1]), parts[2]);
                if (mmoItem != null) {
                    return mmoItem.newBuilder().build();
                }
            }
        }
        Material mat = Material.matchMaterial(itemString);
        return new ItemStack(mat != null ? mat : Material.BARRIER);
    }
}