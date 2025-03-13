package me.bedwarshurts.mmextension.mechanics.canceldeath;

import io.lumine.mythic.api.skills.SkillMetadata;

import java.util.UUID;

public class PlayerDeathData {
    private final UUID player;
    private final double healthPercentage;
    private final String skillName;
    private final SkillMetadata data;

    public PlayerDeathData(UUID player, double healthPercentage, String skillName, SkillMetadata data) {
        this.player = player;
        this.healthPercentage = healthPercentage;
        this.skillName = skillName;
        this.data = data;
    }

    public UUID getPlayer() {
        return player;
    }

    public double getHealthPercentage() {
        return healthPercentage;
    }

    public String getSkill() {
        return skillName;
    }

    public SkillMetadata getData() {
        return data;
    }
}
