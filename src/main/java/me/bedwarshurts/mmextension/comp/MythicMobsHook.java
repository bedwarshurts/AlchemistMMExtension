package me.bedwarshurts.mmextension.comp;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import me.bedwarshurts.mmextension.conditions.StringContainsCondition;
import me.bedwarshurts.mmextension.conditions.YLevelCondition;
import me.bedwarshurts.mmextension.conditions.IsInFactionCondition;
import me.bedwarshurts.mmextension.mechanics.*;
import me.bedwarshurts.mmextension.mechanics.loop.BreakMechanic;
import me.bedwarshurts.mmextension.mechanics.loop.ForEachMechanic;
import me.bedwarshurts.mmextension.mechanics.loop.LoopMechanic;
import me.bedwarshurts.mmextension.mechanics.mmocore.SetMMOCooldownMechanic;
import me.bedwarshurts.mmextension.mechanics.particle.RingShapeMechanic;
import me.bedwarshurts.mmextension.mechanics.particle.SphereShapeMechanic;
import me.bedwarshurts.mmextension.targeters.ConnectedBlocksTargeter;
import me.bedwarshurts.mmextension.targeters.GroundLevelTargeter;
import me.bedwarshurts.mmextension.targeters.LocationPredictingTargeter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsHook implements Listener {

    @EventHandler
    public void loadMechanics(MythicMechanicLoadEvent event) {
        String mechanicName = event.getMechanicName().toLowerCase();

        switch (mechanicName) {
            case "bookgui":
                event.register(new BookGUIMechanic(event.getConfig()));
                break;
            case "sphereshape":
                event.register(new SphereShapeMechanic(event.getContainer().getManager(), event.getConfig()));
                break;
            case "ringshape":
                event.register(new RingShapeMechanic(event.getContainer().getManager(), event.getConfig()));
                break;
            case "cancelplayerdeath":
                event.register(new CancelPlayerDeathMechanic(event.getContainer().getManager(), event.getConfig()));
                break;
            case "openchest":
                event.register(new OpenChestMechanic(event.getConfig()));
                break;
            case "loop":
                event.register(new LoopMechanic(event.getContainer().getManager(), event.getConfig()));
                break;
            case "break":
                event.register(new BreakMechanic(event.getConfig()));
                break;
            case "primedtnt":
                event.register(new PrimedTnTMechanic(event.getConfig()));
                break;
            case "setmmocooldown":
            case "setmmocd":
                event.register(new SetMMOCooldownMechanic(event.getConfig()));
                break;
            case "periodicblockbreak":
            case "periodicbreak":
                event.register(new PeriodicBlockBreakMechanic(event.getContainer().getManager(), event.getConfig()));
                break;
            case "foreach":
                event.register(new ForEachMechanic(event.getContainer().getManager(), event.getConfig()));
                break;
            case "randomizehotbar":
                event.register(new RandomizeHotbarMechanic());
                break;
            case "chestgui":
                event.register(new ChestGUIMechanic(event.getConfig()));
            default: break;
        }
    }

    @EventHandler
    public void loadTargeters(MythicTargeterLoadEvent event) {
        String targeterName = event.getTargeterName().toLowerCase();

        switch (targeterName) {
            case "targetgroundlocation":
            case "tgl":
                event.register(new GroundLevelTargeter(event.getConfig()));
                break;
            case "targetpredictedlocation":
            case "tpl":
                event.register(new LocationPredictingTargeter(event.getConfig()));
                break;
            case "targetconnectedblocks":
            case "tcb":
                event.register(new ConnectedBlocksTargeter(event.getConfig()));
                break;
            default: break;
        }
    }

    @EventHandler
    public void loadConditions(MythicConditionLoadEvent event) {
        String conditionName = event.getConditionName().toLowerCase();

        switch (conditionName) {
            case "stringcontains":
                event.register(new StringContainsCondition(event.getConfig()));
                break;
            case "isylevel":
                event.register(new YLevelCondition(event.getConfig()));
                break;
            case "isinfaction":
                event.register(new IsInFactionCondition(event.getConfig()));
                break;
            default: break;
        }
    }
}