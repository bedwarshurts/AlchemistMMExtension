package me.bedwarshurts.mmextension.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.api.adapters.AbstractEntity;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.stream.Collectors;

@MythicCondition(author = "bedwarshurts", name = "isinfaction", aliases = {}, description = "Check if the entity is in a specified faction")
public class IsInFactionCondition extends SkillCondition implements ISkillMetaCondition {
    private final String faction;
    private final String targeterConfig;

    public IsInFactionCondition(MythicLineConfig mlc) {
        super(mlc.getLine());
        this.faction = mlc.getString("faction", "default");
        this.targeterConfig = mlc.getString("targeter", null);
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        if (targeterConfig != null) {
            IEntitySelector targeter = (IEntitySelector) MythicBukkit.inst().getSkillManager().getTargeter(targeterConfig);
            List<AbstractEntity> targets = targeter.getEntities(skillMetadata).stream().collect(Collectors.toList());
            for (AbstractEntity abstractEntity : targets) {
                Entity entity = abstractEntity.getBukkitEntity();
                if (isInFaction(entity)) {
                    return true;
                }
            }
            return false;
        } else {
            Entity entity = skillMetadata.getCaster().getEntity().getBukkitEntity();
            return isInFaction(entity);
        }
    }

    private boolean isInFaction(Entity entity) {
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