package me.bedwarshurts.mmextension.mechanics.signal;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.listeners.OnSignalListener;
import me.bedwarshurts.mmextension.mechanics.AuraMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.plugin;

@MythicMechanic(author = "bedwarshurts", name = "onsignal", aliases = {}, description = "Triggers a skill when a player receives a signal")
public class OnSignalMechanic implements INoTargetSkill, AuraMechanic {
    private final String identifier;
    private final String skill;
    private final String signal;
    private final long durationTicks;

    public static final Map<String, OnSignalData> ACTIVE_SIGNALS = new HashMap<>();

    public OnSignalMechanic(MythicLineConfig mlc) {
        Bukkit.getPluginManager().registerEvents(new OnSignalListener(), plugin);

        this.identifier = mlc.getString(new String[]{"name", "id", "identifier"}, "signal");
        this.skill = mlc.getString(new String[]{"skill"}, "");
        this.signal = mlc.getString(new String[]{"signal"}, "");
        this.durationTicks = (long) mlc.getDouble(new String[]{"duration", "d"}, 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean success = false;

        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;

            Player player = (Player) target.getBukkitEntity();
            OnSignalData onSignal = new OnSignalData(player, new MythicSkill(skill), signal, data.getCaster());

            ACTIVE_SIGNALS.put(identifier + target.getUniqueId(), onSignal);
            this.register(target.getUniqueId(), this, identifier, (double) durationTicks, onSignal);
            success = true;
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }

    @Override
    public void terminate() {
    }
}