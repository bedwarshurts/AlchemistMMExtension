package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.Map;
import java.util.Set;

@MythicMechanic(author = "bedwarshurts", name = "test", aliases = {}, description = "Test mechanic for debugging purposes")
public class TestMechanic implements INoTargetSkill {
    private String line;
    private final String key;
    private final Set<Map.Entry<String, String>> map;

    public TestMechanic(MythicLineConfig mlc) {
        this.line = mlc.getLine();
        this.key = mlc.getKey();
        this.map = mlc.entrySet();
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        line = PlaceholderUtils.parseStringPlaceholders(line, data);
        line = PlaceholderAPI.setPlaceholders(null, line);
        System.out.println(line);
        System.out.println("-----------------------------");
        System.out.println(key);
        System.out.println("------------------------------");
        for (Map.Entry<String, String> entry : map) {
            System.out.println(entry + " " + entry.getValue());
        }

        return SkillResult.SUCCESS;
    }
}
