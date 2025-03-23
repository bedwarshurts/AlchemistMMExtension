package me.bedwarshurts.mmextension.comp;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import me.bedwarshurts.mmextension.conditions.OxygenLevelCondition;
import me.bedwarshurts.mmextension.conditions.StringContainsCondition;
import me.bedwarshurts.mmextension.conditions.YLevelCondition;
import me.bedwarshurts.mmextension.conditions.IsInFactionCondition;
import me.bedwarshurts.mmextension.conditions.mythicdungeon.IsInDungeonCondition;
import me.bedwarshurts.mmextension.mechanics.*;
import me.bedwarshurts.mmextension.mechanics.canceldeath.CancelPlayerDeathMechanic;
import me.bedwarshurts.mmextension.mechanics.chestgui.ChestGUIMechanic;
import me.bedwarshurts.mmextension.mechanics.inventory.HotbarSnapshotMechanic;
import me.bedwarshurts.mmextension.mechanics.inventory.PlaceToInventoryMechanic;
import me.bedwarshurts.mmextension.mechanics.inventory.RandomizeHotbarMechanic;
import me.bedwarshurts.mmextension.mechanics.inventory.RestoreHotbarMechanic;
import me.bedwarshurts.mmextension.mechanics.list.AddMechanic;
import me.bedwarshurts.mmextension.mechanics.list.CreateVariablesMechanic;
import me.bedwarshurts.mmextension.mechanics.list.GetMechanic;
import me.bedwarshurts.mmextension.mechanics.list.IndexMechanic;
import me.bedwarshurts.mmextension.mechanics.list.ListMechanic;
import me.bedwarshurts.mmextension.mechanics.list.RemoveMechanic;
import me.bedwarshurts.mmextension.mechanics.list.ReplaceMechanic;
import me.bedwarshurts.mmextension.mechanics.list.SizeMechanic;
import me.bedwarshurts.mmextension.mechanics.loop.BreakMechanic;
import me.bedwarshurts.mmextension.mechanics.loop.ForEachMechanic;
import me.bedwarshurts.mmextension.mechanics.loop.WhileLoopMechanic;
import me.bedwarshurts.mmextension.mechanics.mmocore.SetMMOCooldownMechanic;
import me.bedwarshurts.mmextension.mechanics.OnSignalMechanic;
import me.bedwarshurts.mmextension.mechanics.particle.VerticalSlashMechanic;
import me.bedwarshurts.mmextension.mechanics.particle.RingShapeMechanic;
import me.bedwarshurts.mmextension.mechanics.particle.SphereShapeMechanic;
import me.bedwarshurts.mmextension.targeters.ConnectedBlocksTargeter;
import me.bedwarshurts.mmextension.targeters.EntityInSightTargeter;
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
                event.register(new SphereShapeMechanic(event.getConfig()));
                break;
            case "ringshape":
                event.register(new RingShapeMechanic(event.getConfig()));
                break;
            case "cancelplayerdeath":
                event.register(new CancelPlayerDeathMechanic(event.getConfig()));
                break;
            case "openchest":
                event.register(new OpenChestMechanic(event.getConfig()));
                break;
            case "whileloop":
            case "while":
                event.register(new WhileLoopMechanic(event.getConfig()));
                break;
            case "break":
                event.register(new BreakMechanic());
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
                event.register(new PeriodicBlockBreakMechanic(event.getConfig()));
                break;
            case "foreach":
                event.register(new ForEachMechanic(event.getConfig()));
                break;
            case "randomizehotbar":
                event.register(new RandomizeHotbarMechanic());
                break;
            case "chestgui":
                event.register(new ChestGUIMechanic(event.getConfig()));
                break;
            case "onsignal":
                event.register(new OnSignalMechanic(event.getConfig()));
                break;
            case "hotbarsnapshot":
                event.register(new HotbarSnapshotMechanic(event.getConfig()));
                break;
            case "restorehotbar":
                event.register(new RestoreHotbarMechanic());
                break;
            case "list":
                event.register(new ListMechanic(event.getConfig()));
                break;
            case "list:add":
                event.register(new AddMechanic(event.getConfig()));
                break;
            case "list:remove":
                event.register(new RemoveMechanic(event.getConfig()));
                break;
            case "list:replace":
                event.register(new ReplaceMechanic(event.getConfig()));
                break;
            case "list:size":
                event.register(new SizeMechanic(event.getConfig()));
                break;
            case "list:get":
                event.register(new GetMechanic(event.getConfig()));
                break;
            case "list:index":
                event.register(new IndexMechanic(event.getConfig()));
                break;
            case "list:createvariables":
                event.register(new CreateVariablesMechanic(event.getConfig()));
                break;
            case "verticalslash":
                event.register(new VerticalSlashMechanic(event.getConfig()));
                break;
            case "hidechat":
                event.register(new HideChatMechanic(event.getConfig()));
                break;
            case "placetoinventory":
                event.register(new PlaceToInventoryMechanic(event.getConfig()));
                break;
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
            case "targetentityinsight":
            case "teis":
                event.register(new EntityInSightTargeter(event.getConfig()));
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
            case "oxygenlevel":
                event.register(new OxygenLevelCondition(event.getConfig()));
                break;
            case "isindungeon":
                event.register(new IsInDungeonCondition());
                break;
            default: break;
        }
    }
}