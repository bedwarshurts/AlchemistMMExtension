package me.bedwarshurts.mmextension.skills.placeholders;

import io.lumine.mythic.core.skills.placeholders.types.GeneralPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "Random Color Placeholder by bedwarshurts", description = "Returns a random color between two hex values.")
public class RandomColorPlaceholder implements GeneralPlaceholder {

    @Override
    public String apply(String s) {
        String[] parts = s.split("to");
        if (parts.length == 2) {
            try {
                int hex1 = Integer.parseInt(parts[0], 16);
                int hex2 = Integer.parseInt(parts[1], 16);

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
        return "[Invalid Placeholder]";
    }
}
