package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
        String key = itemString.trim().toLowerCase(Locale.ROOT);
        String[] parts = itemString.split(":", 3);

        if (key.startsWith("mmoitem:")) {
            if (!PluginHooks.isInstalled(PluginHooks.MMOItems))
                throw new DependencyNotFoundException("MMOItems is not installed!");

            if (parts.length != 3)
                throw new IllegalArgumentException("Invalid MMOItem format. Expected format: mmoitem:<category>:<id>");

            MMOItem mmoItem = MMOItems.plugin.getMMOItem(Type.get(parts[1]), parts[2]);
            if (mmoItem != null) {
                return mmoItem.newBuilder().build();
            }

            throw new IllegalArgumentException("MMOItem not found: " + itemString);
        }

        if (key.startsWith("mythic:")) {
            if (parts.length != 2)
                throw new IllegalArgumentException("Invalid Mythic Item format. Expected format: mythic:<item_name>");

            Optional<MythicItem> optionalItem = MythicBukkit.inst()
                    .getItemManager()
                    .getItem(parts[1]);
            if (optionalItem.isPresent())
                return BukkitAdapter.adapt(optionalItem.get().generateItemStack(1));

            AlchemistMMExtension.inst().getLogger().warning("Mythic Item not found: " + parts[1]);
            return null;
        }

        Material mat = Material.matchMaterial(itemString);
        return new ItemStack(mat != null ? mat : Material.BARRIER);
    }

    public static ItemStack buildItem(String itemInfo) {
        String materialPart = itemInfo.contains("[") ? itemInfo.substring(0, itemInfo.indexOf("[")).trim() : itemInfo.trim();
        String bracketContent = itemInfo.contains("[") ? itemInfo.substring(itemInfo.indexOf("[") + 1, itemInfo.lastIndexOf("]")) : "";
        Map<String, String> itemMetadata = ItemUtils.parse(bracketContent);

        ItemStack item = ItemUtils.getItemStack(materialPart);
        if (item == null) item = new ItemStack(Material.BARRIER);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

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

        // Item Model
        String itemModelValue = itemMetadata.getOrDefault("itemModel", "");
        if (!itemModelValue.isEmpty()) {
            String namespace = "minecraft";
            String modelName = itemModelValue;

            int colonIdx = itemModelValue.indexOf(':');
            if (colonIdx > 0 && colonIdx < itemModelValue.length() - 1) {
                namespace = itemModelValue.substring(0, colonIdx);
                modelName = itemModelValue.substring(colonIdx + 1);
            }

            NamespacedKey key = new NamespacedKey(namespace, modelName);
            meta.setItemModel(key);
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
            NamespacedKey key = new NamespacedKey(AlchemistMMExtension.inst(), "skill");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, skillValue);
        }
        item.setItemMeta(meta);
        return item;
    }
}
