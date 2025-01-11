package me.bedwarshurts.mmextension.utils;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utilities {
    private static final Pattern placeholderPattern = Pattern.compile("<(world|caster|target|skill)(\\.var)?\\.[a-zA-Z0-9]+>");

    public static String parseStringPlaceholders(String text, SkillMetadata skillMetadata) {
        boolean found;
        do {
            Matcher matcher = placeholderPattern.matcher(text);
            StringBuffer result = new StringBuffer();
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
