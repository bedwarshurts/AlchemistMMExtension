package me.bedwarshurts.mmextension.comp;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.mechanics.MetaSkillMechanic;

import java.io.File;
import java.util.Map;

public class AlchemistSkillManager extends SkillExecutor {

    private final Map<String, Class<? extends SkillMechanic>> MECHANICS = super.getMechanics();

    public AlchemistSkillManager(MythicBukkit inst) {
        super(inst);
    }

    @Override
    public SkillMechanic getMechanic(Pack pack, File file, SkillHolder parent, String skillLine) {
        MythicLogger.debug(MythicLogger.DebugLevel.INFO, "[AlchemistSkillExecutor] Parsing skill line: {0}", skillLine);

        String mechanicPart = skillLine;
        String extraPart = "";

        int conditionIndex = skillLine.indexOf(" ?");
        int targeterIndex = skillLine.indexOf(" @");
        int triggerIndex = skillLine.indexOf(" ~");

        int firstSpecialIndex = Integer.MAX_VALUE;
        if (conditionIndex != -1) firstSpecialIndex = conditionIndex;
        if (targeterIndex != -1) firstSpecialIndex = Math.min(firstSpecialIndex, targeterIndex);
        if (triggerIndex != -1) firstSpecialIndex = Math.min(firstSpecialIndex, triggerIndex);

        if (firstSpecialIndex != Integer.MAX_VALUE) {
            mechanicPart = skillLine.substring(0, firstSpecialIndex);
            extraPart = skillLine.substring(firstSpecialIndex);
        }

        MythicLogger.debug(MythicLogger.DebugLevel.INFO, "[AlchemistSkillExecutor] Mechanic part: {0}", mechanicPart);
        MythicLogger.debug(MythicLogger.DebugLevel.INFO, "[AlchemistSkillExecutor] Extra part: {0}", extraPart);

        String mechanicName;

        if (!mechanicPart.contains("{")) {
            int firstSpace = mechanicPart.indexOf(' ');
            if (firstSpace != -1) {
                mechanicName = mechanicPart.substring(0, firstSpace);
                System.out.println(mechanicName);
                if (mechanicName.equalsIgnoreCase("skill")) {
                    mechanicName = "skillvariable";
                }
            } else {
                mechanicName = mechanicPart;
            }
        } else {
            mechanicName = mechanicPart.substring(0, mechanicPart.indexOf("{"));
        }

        MythicLineConfig mlc = (file != null)
                ? new MythicLineConfigImpl(file, mechanicPart.trim())
                : new MythicLineConfigImpl(mechanicPart.trim());


        if (MECHANICS.containsKey(mechanicName.toUpperCase())) {
            Class<? extends SkillMechanic> clazz = MECHANICS.get(mechanicName.toUpperCase());

            try {
                SkillMechanic mechanic = clazz.getConstructor(SkillExecutor.class, File.class, String.class, MythicLineConfig.class)
                        .newInstance(this, file, skillLine, mlc);
                mechanic.setPack(pack);
                mechanic.setParent(parent);
                return mechanic;
            } catch (Exception e) {
                MythicLogger.error("[AlchemistSkillExecutor] Failed to construct mechanic {0}", skillLine);
                throw new UnsupportedOperationException(e);
            }
        }

        try {
            if (!mechanicName.toUpperCase().startsWith("SKILL:") && !mechanicName.toUpperCase().startsWith("META:")) {
                SkillMechanic mechanic = new CustomMechanic(this, file, mechanicName.toUpperCase(), skillLine, mlc);
                mechanic.setPack(pack);
                mechanic.setParent(parent);
                return mechanic;
            }
            String skillName = mechanicName.substring(mechanicName.indexOf(":") + 1);
            SkillMechanic mechanic = new MetaSkillMechanic(this, file, skillLine, skillName, mlc);
            mechanic.setPack(pack);
            mechanic.setParent(parent);
            return mechanic;
        } catch (Exception e) {
            MythicLogger.error("[AlchemistSkillExecutor] Failed to load skill line due to bad syntax: {0}", skillLine);
            MythicLogger.handleMinorError(e);
            return null;
        }
    }
}
