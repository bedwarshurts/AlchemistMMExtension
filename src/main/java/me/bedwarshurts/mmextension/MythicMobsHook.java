package me.bedwarshurts.mmextension;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import me.bedwarshurts.mmextension.conditions.StringContainsCondition;
import me.bedwarshurts.mmextension.conditions.YLevelCondition;
import me.bedwarshurts.mmextension.conditions.IsInFactionCondition;
import me.bedwarshurts.mmextension.mechanics.BookGUIMechanic;
import me.bedwarshurts.mmextension.mechanics.SphereShapeMechanic;
import me.bedwarshurts.mmextension.mechanics.CancelPlayerDeathMechanic;
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
                event.register(new SphereShapeMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getMechanicName(), event.getConfig()));
                break;
            case "cancelplayerdeath":
                event.register(new CancelPlayerDeathMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getMechanicName(), event.getConfig()));
                break;
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
