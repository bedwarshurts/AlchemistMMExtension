package me.bedwarshurts.mmextension.mechanics.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@SuppressWarnings("deprecation")
public class InventorySerializer {

    public static String toBase64(ItemStack[] items) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytesOut)) {
            out.writeInt(items.length);
            for (ItemStack item : items) {
                out.writeObject(item);
            }
        }
        return Base64.getEncoder().encodeToString(bytesOut.toByteArray());
    }

    public static ItemStack[] fromBase64(String data) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ItemStack[] items;
        try (BukkitObjectInputStream in = new BukkitObjectInputStream(bytesIn)) {
            items = new ItemStack[in.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) in.readObject();
            }
        }
        return items;
    }
}