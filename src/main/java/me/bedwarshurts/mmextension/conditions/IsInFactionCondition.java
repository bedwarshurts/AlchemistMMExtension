package me.bedwarshurts.mmextension.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Entity;

@MythicCondition(author = "bedwarshurts", name = "isinfaction", aliases = {}, description = "Check if the entity is in any of the specified factions")
public class IsInFactionCondition implements IEntityCondition {
    private final String[] factions;

    public IsInFactionCondition(MythicLineConfig mlc) {
        this.factions = mlc.getString("factions", "default").toLowerCase().split(",");
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        Entity entity = abstractEntity.getBukkitEntity();
        ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
        if (mob == null || !mob.hasFaction()) {
            return false;
        }

        String[] mobFactions = mob.getFaction().toLowerCase().split(",");
        for (String mobFaction : mobFactions) {
            for (String faction : factions) {
                if (mobFaction.trim().equalsIgnoreCase(faction.trim())) {
                    return true;
                }
            }
        }
        return false;
    }
}