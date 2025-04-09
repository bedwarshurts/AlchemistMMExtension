package me.bedwarshurts.mmextension.skills.triggers;

import io.lumine.mythic.api.skills.SkillTrigger;
import me.bedwarshurts.mmextension.skills.triggers.meta.PreAttackEntityMeta;

public class MoreSkillTriggers {
    public static final SkillTrigger<?> PRE_ATTACK =
            SkillTrigger.create("PRE_ATTACK", PreAttackEntityMeta.class, "PREATTACK", "PRE_ATTACK", "PREATTACKENTITY", "PRE_ATTACK_ENTITY");

    public static void registerTriggers() {
        PRE_ATTACK.register();
    }
}
