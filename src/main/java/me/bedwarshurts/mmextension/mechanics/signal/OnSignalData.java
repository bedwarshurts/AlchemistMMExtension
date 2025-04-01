package me.bedwarshurts.mmextension.mechanics.signal;

import io.lumine.mythic.api.skills.SkillCaster;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.DataHolder;
import org.bukkit.entity.Player;

public class OnSignalData implements DataHolder {
    private final Player player;
    private final MythicSkill skill;
    private final String signal;
    private final SkillCaster caster;

    public OnSignalData(Player player, MythicSkill skill, String signal, SkillCaster caster) {
        this.player = player;
        this.skill = skill;
        this.signal = signal;
        this.caster = caster;
    }

    public Player getPlayer() {
        return player;
    }

    public MythicSkill getSkill() {
        return skill;
    }

    public String getSignal() {
        return signal;
    }

    public SkillCaster getCaster() {
        return caster;
    }

}
