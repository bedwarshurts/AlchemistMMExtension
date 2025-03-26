package me.bedwarshurts.mmextension.comp;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "alchemist";
    }

    @Override
    public @NotNull String getAuthor() {
        return "bedwarshurts";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("color_")) {
            String[] parts = identifier.split("_");
            if (parts.length == 3) {
                try {
                    int hex1 = Integer.parseInt(parts[1], 16);
                    int hex2 = Integer.parseInt(parts[2], 16);

                    int r1 = (hex1 >> 16) & 0xFF;
                    int g1 = (hex1 >> 8) & 0xFF;
                    int b1 = hex1 & 0xFF;

                    int r2 = (hex2 >> 16) & 0xFF;
                    int g2 = (hex2 >> 8) & 0xFF;
                    int b2 = hex2 & 0xFF;

                    int r = r1 + (int) (Math.round(Math.abs((r2 - r1) * Math.random())));
                    int g = g1 + (int) (Math.round(Math.abs((g2 - g1) * Math.random())));
                    int b = b1 + (int) (Math.round(Math.abs((b2 - b1) * Math.random())));

                    return String.format("#%02X%02X%02X", r, g, b);
                } catch (NumberFormatException e) {
                    return "Invalid hex format";
                }
            }
        }
        if (identifier.startsWith("string_replace_")) {
            String[] parts = identifier.split("_", 5);
            if (parts.length == 5) {
                String inputString = parts[2];
                String oldString = parts[3];
                String newString = parts[4];

                int index = inputString.lastIndexOf(oldString);
                if (index != -1) {
                    StringBuilder sb = new StringBuilder(inputString);
                    sb.replace(index, index + oldString.length(), newString);
                    return sb.toString();
                }
                return inputString;
            }
            return "Invalid format";
        }
        return null;
    }
}