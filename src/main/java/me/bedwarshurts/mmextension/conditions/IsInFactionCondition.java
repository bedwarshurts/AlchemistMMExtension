package me.bedwarshurts.mmextension.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

@MythicCondition(author = "bedwarshurts", name = "isinfaction", aliases = {}, description = "Check if the entity is in a specified faction")
public class IsInFactionCondition extends SkillCondition implements ISkillMetaCondition {
    private final String faction;

    public IsInFactionCondition(MythicLineConfig mlc) {
        super(mlc.getLine());
        this.faction = mlc.getString("faction", "default");
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        Entity entity = skillMetadata.getCaster().getEntity().getBukkitEntity();
        ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
        if (mob == null || !mob.hasFaction()) {
            return false;
        }

        String[] factions = mob.getFaction().toLowerCase().split(",");
        for (String fac : factions) {
            if (fac.trim().equalsIgnoreCase(faction)) {
                return true;
            }
        }
        return false;
    }
}