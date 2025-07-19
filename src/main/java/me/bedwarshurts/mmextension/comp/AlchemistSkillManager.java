package me.bedwarshurts.mmextension.comp;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.core.config.IOHandler;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import io.lumine.mythic.core.skills.mechanics.MetaSkillMechanic;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlchemistSkillManager extends SkillExecutor {

    private final Map<String, Class<? extends SkillMechanic>> MECHANICS = super.getMechanics();

    public AlchemistSkillManager(MythicBukkit inst) {
        super(inst);
    }

    @Override
    public void loadSkills() {
        MythicLogger.log("Loading Skills...");
        
        IOLoader<MythicBukkit> defaultSkills = new IOLoader<>(MythicBukkit.inst(), "ExampleSkills.yml", "Skills");

        try {
            Field skillsField = SkillExecutor.class.getDeclaredField("skills");
            skillsField.setAccessible(true);
            Map<?,?> baseMap = (Map<?,?>) skillsField.get(this);
            baseMap.clear();
        } catch (Exception e) {
            MythicLogger.error("[AlchemistSkillExecutor] Could not clear base skills map", e);
        }

        Pattern definePattern = Pattern.compile("^#define\\s+(\\w+)\\s+(.*)$");

        for (Pack pack : this.getPlugin().getPackManager().getPacks()) {
            for (File folder : pack.getPackFolders("Skills", true, true)) {
                for (File file : io.lumine.mythic.bukkit.utils.files.Files.getAll(folder.getAbsolutePath(), Lists.newArrayList("yml", "txt"))) {
                    try {
                        List<String> lines = java.nio.file.Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                        Map<String, String> defines = new LinkedHashMap<>();
                        int startIndex = 0;

                        while (startIndex < lines.size()) {
                            Matcher matcher = definePattern.matcher(lines.get(startIndex));
                            if (!matcher.matches()) {
                                break;
                            }
                            defines.put(matcher.group(1), matcher.group(2));
                            startIndex++;
                        }

                        // rebuild yaml after define cuz rito gems
                        StringBuilder contentBuilder = new StringBuilder();
                        for (int i = startIndex; i < lines.size(); i++) {
                            contentBuilder.append(lines.get(i)).append("\n");
                        }
                        String yamlContent = contentBuilder.toString();

                        for (Map.Entry<String, String> define : defines.entrySet()) {
                            String key = define.getKey();
                            String value = define.getValue();
                            yamlContent = yamlContent.replaceAll("\\b" + Pattern.quote(key) + "\\b", Matcher.quoteReplacement(value));
                        }

                        YamlConfiguration yamlConfig = new YamlConfiguration();
                        yamlConfig.loadFromString(yamlContent);
                        MythicConfigImpl skillConfig = new MythicConfigImpl("", file, yamlConfig);

                        for (String node : NodeListProp.getNodes(file, "")) {
                            try {
                                MythicConfig config = skillConfig.getNestedConfig(node);
                                if (!folder.getName().equals("mono") || (!config.isSet("Id") && !config.isSet("Type"))) {
                                    MetaSkill skill = new MetaSkill(this, pack, file, node, config);
                                    this.registerSkill(node, skill);
                                }
                            } catch (Exception nodeEx) {
                                MythicLogger.error("[AlchemistSkillExecutor] Error loading skill '" + node + "'. Enable debugging for stack trace.");
                                MythicLogger.handleMinorError(nodeEx);
                            }
                        }
                    } catch (IOException | InvalidConfigurationException ex) {
                        MythicLogger.error("[AlchemistSkillExecutor] Failed to preprocess skill file: " + file.getName());
                        MythicLogger.handleMinorError(ex);
                    }
                }
            }
        }
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
        String mechanicName = getString(mechanicPart);

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

    private @NotNull String getString(String mechanicPart) {
        String mechanicName;

        if (!mechanicPart.contains("{")) {
            int firstSpace = mechanicPart.indexOf(' ');
            if (firstSpace != -1) {
                mechanicName = mechanicPart.substring(0, firstSpace);
                if (mechanicName.equalsIgnoreCase("skill")) {
                    mechanicName = "skillvariable";
                }
            } else {
                mechanicName = mechanicPart;
            }
        } else {
            mechanicName = mechanicPart.substring(0, mechanicPart.indexOf("{"));
        }
        return mechanicName;
    }

    @Override
    public ImmutableMap<String, Class<? extends SkillMechanic>> getMechanics() {
        return ImmutableMap.copyOf(MECHANICS);
    }

}
