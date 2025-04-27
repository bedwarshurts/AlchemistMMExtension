package me.bedwarshurts.mmextension.skills.triggers;

import io.lumine.mythic.api.skills.SkillTrigger;
import me.bedwarshurts.mmextension.skills.triggers.meta.PlayerChangeSlotMeta;
import me.bedwarshurts.mmextension.skills.triggers.meta.PreAttackEntityMeta;
import me.bedwarshurts.mmextension.skills.triggers.meta.SkillCastMeta;

public class MoreSkillTriggers {

    public static final SkillTrigger<?> PRE_ATTACK =
            SkillTrigger.create("PRE_ATTACK", PreAttackEntityMeta.class, "PREATTACK", "PRE_ATTACK", "PREATTACKENTITY", "PRE_ATTACK_ENTITY");
    public static final SkillTrigger<?> SKILL_CAST =
            SkillTrigger.create("SKILL_CAST", SkillCastMeta.class, "SKILLCAST", "SKILL_CAST", "MMOSKILLCAST", "MMO_SKILL_CAST");
    public static final SkillTrigger<?> PLAYER_CHANGE_SLOT =
            SkillTrigger.create("PLAYER_CHANGE_SLOT", PlayerChangeSlotMeta.class, "PLAYERCHANGESLOT", "PLAYER_CHANGE_SLOT", "CHANGE_SLOT", "CHANGESLOT");

    public static void registerTriggers() {
        PRE_ATTACK.register();
        SKILL_CAST.register();
        PLAYER_CHANGE_SLOT.register();
    }
}
