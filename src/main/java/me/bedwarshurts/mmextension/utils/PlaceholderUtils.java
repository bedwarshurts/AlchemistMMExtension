package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import static me.bedwarshurts.mmextension.AlchemistMMExtension.AlchemistMMExtension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtils {
    private static final Pattern placeholderPattern = Pattern.compile("<(world|caster|target|skill)(\\.var)?\\.[a-zA-Z0-9]+>");

    public static String parseStringPlaceholders(String text, SkillMetadata skillMetadata) {
        boolean found;
        do {
            Matcher matcher = placeholderPattern.matcher(text);
            StringBuilder result = new StringBuilder();
            found = false;

            while (matcher.find()) {
                found = true;
                String placeholder = matcher.group(0);
                String replacement = PlaceholderString.of(placeholder).get(skillMetadata);
                matcher.appendReplacement(result, replacement);
            }
            matcher.appendTail(result);
            text = result.toString();
        } while (found);

        return text;
    }

    public static double parseStatPlaceholder(String placeholder) {
        try {
            return Double.parseDouble(placeholder);
        } catch (NumberFormatException e) {
            AlchemistMMExtension.getLogger().warning("Invalid placeholder value for " + placeholder);
            return 0;
        }
    }

    public static String parseMythicTags(String text) {
        text = text.replaceAll("<&sp>", " ");
        text = text.replaceAll("<&sq>", "'");
        text = text.replaceAll("<&bs>", "\"");
        text = text.replaceAll("<&co>", ":");
        text = text.replaceAll("<&cm>", ",");
        text = text.replaceAll("<&da>", "-");

        return text;
    }
}
