package me.bedwarshurts.mmextension.comp;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import me.bedwarshurts.mmextension.skills.conditions.OxygenLevelCondition;
import me.bedwarshurts.mmextension.skills.conditions.StringContainsCondition;
import me.bedwarshurts.mmextension.skills.conditions.YLevelCondition;
import me.bedwarshurts.mmextension.skills.conditions.IsInFactionCondition;
import me.bedwarshurts.mmextension.skills.mechanics.BookGUIMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.HideChatMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.MirrorPlayerSkinMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.OpenChestMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.PeriodicBlockBreakMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.PlaceBlockMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.PrimedTnTMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.SetWorldBorderMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.StringBuilderMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.aura.events.EventSubscribeMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.aura.CancelPlayerDeathMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.aura.events.InvokeMethodMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.chestgui.ChestGUIMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.inventory.HotbarSnapshotMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.inventory.PlaceToInventoryMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.inventory.RandomizeHotbarMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.inventory.RestoreHotbarMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.AddMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.CreateVariablesMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.GetMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.IndexMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.ListMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.RemoveMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.ReplaceMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.list.SizeMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.loop.BreakMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.loop.ForEachMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.loop.WhileLoopMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.mmocore.SetMMOCooldownMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.aura.OnSignalMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.particle.VerticalSlashMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.particle.RingShapeMechanic;
import me.bedwarshurts.mmextension.skills.mechanics.particle.SphereShapeMechanic;
import me.bedwarshurts.mmextension.skills.targeters.ConnectedBlocksTargeter;
import me.bedwarshurts.mmextension.skills.targeters.EntityInSightTargeter;
import me.bedwarshurts.mmextension.skills.targeters.GroundLevelTargeter;
import me.bedwarshurts.mmextension.skills.targeters.LocationPredictingTargeter;
import me.bedwarshurts.mmextension.skills.targeters.EntityByClassInRadiusTargeter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class MythicMobsHook implements Listener {

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
                event.register(new CancelPlayerDeathMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getConfig()));
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
                event.register(new OnSignalMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getConfig()));
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
            case "events:subscribe":
            case "events:sub":
                event.register(new EventSubscribeMechanic(event.getContainer().getManager(), event.getContainer().getFile(), event.getConfig()));
                break;
            case "setworldborder":
            case "swb":
                event.register(new SetWorldBorderMechanic(event.getConfig()));
                break;
            case "stringbuilder":
            case "sbuilder":
                event.register(new StringBuilderMechanic(event.getConfig()));
                break;
            case "mirrorplayerskin":
            case "mirrorskin":
                event.register(new MirrorPlayerSkinMechanic());
                break;
            case "placeblock":
            case "pb":
                event.register(new PlaceBlockMechanic(event.getConfig()));
                break;
            case "events:invoke":
            case "events:invokemethod":
                event.register(new InvokeMethodMechanic(event.getConfig()));
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
            case "tpl2":
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
            case "targetentitiesinradius":
            case "teir":
                event.register(new EntityByClassInRadiusTargeter(event.getConfig()));
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
            /* case "isindungeon":
             *   event.register(new IsInDungeonCondition());
             *   break;
             */
            default: break;
        }
    }
}