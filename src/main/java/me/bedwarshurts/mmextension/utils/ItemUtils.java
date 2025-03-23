package me.bedwarshurts.mmextension.utils;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtils {

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
