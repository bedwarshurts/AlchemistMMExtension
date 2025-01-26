package me.bedwarshurts.mmextension.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

public class IsInFactionCondition implements IEntityCondition {
    private final String faction;

    public IsInFactionCondition(MythicLineConfig mlc) {
        this.faction = mlc.getString("faction", "default");
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        Entity entity = abstractEntity.getBukkitEntity();
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