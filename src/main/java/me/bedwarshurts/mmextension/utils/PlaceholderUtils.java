package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import me.bedwarshurts.mmextension.AlchemistMMExtension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceholderUtils {
    private static final Pattern placeholderPattern = Pattern.compile("<(world|caster|target|skill)(\\.var)?\\.[a-zA-Z0-9.()]+>");

    private PlaceholderUtils() {
        throw new UnsupportedOperationException("You really shouldnt initialise this class");
    }

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

    public static String parseDoublePlaceholders(String text, SkillMetadata skillMetadata) {
        Matcher matcher = placeholderPattern.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String replacement = String.valueOf(PlaceholderDouble.of(placeholder).get(skillMetadata));
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static String parseIntPlaceholders(String text, SkillMetadata skillMetadata) {
        Matcher matcher = placeholderPattern.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String replacement = String.valueOf(PlaceholderInt.of(placeholder).get(skillMetadata));
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static double parseStatPlaceholder(String placeholder) {
        try {
            return Double.parseDouble(placeholder);
        } catch (NumberFormatException e) {
            AlchemistMMExtension.inst().getLogger().warning("Invalid placeholder value for " + placeholder);
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
