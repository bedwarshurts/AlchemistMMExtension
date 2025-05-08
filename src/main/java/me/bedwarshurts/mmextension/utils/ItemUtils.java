package me.bedwarshurts.mmextension.utils;

import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.comp.PluginHooks;
import me.bedwarshurts.mmextension.utils.exceptions.DependencyNotFoundException;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ItemUtils {

    private ItemUtils() {
        throw new UnsupportedOperationException("You really shouldnt initialise this class");
    }

    public static Map<String, String> parse(String content) {
        Map<String, String> map = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)=([^,\\]]*)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group(1).trim().toLowerCase();
            String value = matcher.group(2).trim().replaceAll("^\"|\"$", "");
            map.put(key, value);
        }
        return map;
    }

    public static ItemStack getItemStack(String itemString) {
        if (itemString.toLowerCase().startsWith("mmoitem:")) {
            if (!PluginHooks.isInstalled(PluginHooks.MMOItems)) throw new DependencyNotFoundException("MMOItems is not installed!");
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

    public static ItemStack buildItem(String itemInfo) {
        String materialPart = itemInfo.contains("[") ? itemInfo.substring(0, itemInfo.indexOf("[")).trim() : itemInfo.trim();
        String bracketContent = itemInfo.contains("[") ? itemInfo.substring(itemInfo.indexOf("[") + 1, itemInfo.lastIndexOf("]")) : "";
        Map<String, String> itemMetadata = ItemUtils.parse(bracketContent);

        Material material = Material.matchMaterial(materialPart.toUpperCase());
        if (material == null) material = Material.BARRIER;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // Name
        String nameValue = itemMetadata.getOrDefault("name", "");
        if (!nameValue.isEmpty()) {
            meta.displayName(MiniMessage.miniMessage().deserialize(nameValue));
        }

        // Lore
        String loreValue = itemMetadata.getOrDefault("lore", "");
        if (!loreValue.isEmpty()) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : loreValue.split("\\\\n")) {
                if (!line.isEmpty()) {
                    loreComponents.add(MiniMessage.miniMessage().deserialize(line));
                }
            }
            meta.lore(loreComponents);
        }

        // Custom model data
        String cmdValue = itemMetadata.getOrDefault("customModelData", "");
        if (!cmdValue.isEmpty()) {
            try {
                meta.setCustomModelData(Integer.parseInt(cmdValue));
            } catch (NumberFormatException e) {
                throw new ArithmeticException("Does that look like a number to you??? " + cmdValue);
            }
        }

        // Enchants
        String enchantsValue = itemMetadata.getOrDefault("enchants", "");
        if (!enchantsValue.isEmpty()) {
            String[] pairs = enchantsValue.split(",");
            for (String pair : pairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));
                    int level = Integer.parseInt(parts[1]);
                    if (enchant != null) {
                        meta.addEnchant(enchant, level, true);
                    }
                }
            }
        }

        // Skills
        String skillValue = itemMetadata.getOrDefault("skill", "");
        if (!skillValue.isEmpty()) {
            NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(AlchemistMMExtension.class), "skill");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, skillValue);
        }
        item.setItemMeta(meta);
        return item;
    }
}
