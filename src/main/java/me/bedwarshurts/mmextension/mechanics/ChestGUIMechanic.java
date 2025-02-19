package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
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

    public static final Map<Inventory, Map<Integer, Map<String, String>>> INVENTORY_ACTIONS = new HashMap<>();
    public static final Map<Inventory, SkillMetadata> INVENTORY_METADATA = new HashMap<>();

    public ChestGUIMechanic(MythicLineConfig config) {
        this.title       = config.getString("title", "Chest GUI");
        this.slots       = Math.max(9, config.getInteger("slots", 9));
        this.rawContents = config.getString("contents", "");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        for (AbstractEntity abstractEntity : data.getEntityTargets()) {
            if (!abstractEntity.isPlayer()) continue;
            Player player = (Player) abstractEntity.getBukkitEntity();

            String parsedTitle = PlaceholderUtils.parseStringPlaceholders(title, data);
            parsedTitle        = PlaceholderAPI.setPlaceholders(player, parsedTitle);

            Inventory inv = Bukkit.createInventory(null, slots, MiniMessage.miniMessage().deserialize(parsedTitle));
            INVENTORY_METADATA.put(inv, data);

            Map<Integer, Map<String, String>> slotActions = new HashMap<>();

            String[] items = rawContents.split("],");
            for (String itemString : items) {
                itemString = itemString.trim();

                if (!itemString.contains("[")) continue;

                String materialPart = itemString.substring(0, itemString.indexOf('[')).trim().toUpperCase();
                Material mat = Material.matchMaterial(materialPart);

                if (mat == null) mat = Material.BARRIER;

                String bracketContent = itemString.substring(itemString.indexOf('[') + 1).replace("]", "");
                Map<String, String> infoMap = parseBracketContent(bracketContent);

                String parsedName = PlaceholderUtils.parseStringPlaceholders(infoMap.getOrDefault("name", ""), data);
                parsedName        = PlaceholderAPI.setPlaceholders(player, parsedName);

                String parsedLore = PlaceholderUtils.parseStringPlaceholders(infoMap.getOrDefault("lore", ""), data);
                parsedLore        = PlaceholderAPI.setPlaceholders(player, parsedLore);

                Component displayName = MiniMessage.miniMessage().deserialize(parsedName);
                List<Component> loreComponents = new ArrayList<>();

                for (String line : parsedLore.split("\\\\n")) {
                    loreComponents.add(MiniMessage.miniMessage().deserialize(line));
                }

                ItemStack stack = new ItemStack(mat);
                ItemMeta  meta  = stack.getItemMeta();

                meta.displayName(displayName);

                if (!loreComponents.isEmpty()) {
                    meta.lore(loreComponents);
                }

                if ("true".equalsIgnoreCase(infoMap.get("enchanted"))) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                }

                stack.setItemMeta(meta);

                int slot = Integer.parseInt(infoMap.getOrDefault("slot", "0"));
                if (slot < 0 || slot >= slots) continue;

                inv.setItem(slot, stack);

                Map<String, String> actions = new HashMap<>();
                addAction("right_click_action", infoMap, actions);
                addAction("left_click_action", infoMap, actions);
                addAction("interact", infoMap, actions);

                slotActions.put(slot, actions);
            }
            INVENTORY_ACTIONS.put(inv, slotActions);
            player.openInventory(inv);
        }
        return SkillResult.SUCCESS;
    }

    private Map<String, String> parseBracketContent(String content) {
        Map<String, String> map = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)=(.*?)(?:,|$)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key   = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            value = value.replaceAll("^\"|\"$", "");
            map.put(key.toLowerCase(), value);
        }
        return map;
    }

    private void addAction(String actionKey, Map<String, String> infoMap, Map<String, String> actions) {
        if (infoMap.containsKey(actionKey)) {
            actions.put(actionKey, infoMap.get(actionKey));
        }
    }
}