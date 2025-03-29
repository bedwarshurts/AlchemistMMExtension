
<div align="center">

# Alchemist MM Extension

![server](https://img.shields.io/badge/play.alchemistnetwork.org-light_green)
![version](https://img.shields.io/badge/version-1.21.x-blue)
[![Build](https://github.com/bedwarshurts/AlchemistMMExtension/actions/workflows/maven.yml/badge.svg)](https://github.com/bedwarshurts/AlchemistMMExtension/actions/workflows/maven.yml)
![CodeQuality](https://img.shields.io/codefactor/grade/github/bedwarshurts/AlchemistMMExtension?style=flat&logo=codefactor&logoSize=auto)
[![Discord](https://img.shields.io/discord/553890347470553088?style=flat&logo=discord&logoColor=%23FFFFFF&label=%20%20%20&labelColor=5865F2&color=5865F2)](https://alchemistnetwork.org/discord)

</div>

An extension for MythicMobs that adds some Mechanics, Targeters, Conditions and utilises the faction feature to apply bonus damage to mobs.

## Mechanics

### BookGUIMechanic

```
- bookgui{contents=; author=; title=;}
```

### CancelPlayerDeathMechanic

```
- cancelplayerdeath{healthPercentage=; skill=;}
```

### SphereShapeMechanic

```
- sphereshape{particle=; radius=; count=; dirMultiplier=; shiftRadius=; variance=; direction=; speed=; skill=; interval=;}
```

### OpenChestMechanic

```
- openchest{action=;}
```

### LoopMechanic

```
- loop{condition=; skill=; interval=; onStart=; onEnd=;}
```

### BreakMechanic

Only works inside the loop mechanic and stops it the moment its called.

```
- break
```

### RingShapeMechanic

```
- ringshape{particle=; radius=; count=; dirMultiplier=; shiftRadius=; variance=; direction=; speed=; skill=; interval=; rotation=; rotMultiplier=; matchRotation=;}
```

### PrimedTntMechanic

```
- primedtnt{fuse=; break=; damage=;}
```

### PeriodicBreakMechanic

```
- periodicbreak{interval=; block=; startingLocation=; skill=;}
```

### ForEachMechanic

This mechanic loops through the locations returned by a targeter and executes the skill sequence for each one. While this will also work with single location returning targeters, it will serve no value.
```
- foreach{interval=; skill=;}
```

### RandomizeHotbarMechanic

```
- randomizehotbar{}
```

### ChestGUIMechanic

```
- chestgui{contents=stone[name=,lore=,slot=,enchanted=,interact=,right_click_action=skill:skillName],mmoitem:ITEM_CATEGORY:ITEM_NAME[]; title=; slots=;}
```

### OnSignalMechanic (Aura)

```
- onsignal{skill=; signal=; duration=;} When the player receives a signal if the aura is active for said
 player the skill will execute with them being the caster
```

### OnSignalRemoveMechanic (Aura)

```
- onsignalremove{skill=; signal=; duration=;} When the player receives a signal if the aura is active for said
 player the skill will execute with them being the caster
```

### ListMechanic

This introduces a new variable type a list, currently the types supported are STRING, INTEGER and DOUBLE

```
- list{name=; type=;}
- list:add{name=; value=;}
- list:get{name=; index=;} The result will be placed in the skill scoped <returnResult> variable
- list:index{name=; value=;last=;} The result will be placed in the skill scoped <returnResult> variable
- list:remove{name=; index=;}
- list:replace{name=; index=; value=;}
- list:size{name=;} The result will be placed in the skill scoped <returnResult> variable
```

### EventsSubscribeMechanic (Aura)

```
- events:subscribe{listenerIdentifier=; class=; skill=; priority=; methods=; triggerMethod=; cancel=; requirePlayer=; duration=;} 
When the entity returned by a @EntityTargeter triggers the specified event, a mythicmobs skill will cast. If a method returns a value (not void)
the result is placed in the skill scoped <(methodName)Result> variable.
```

### EventsUnSubscribeMechanic (Aura)

```
- events:subscribe{listenerIdentifier=;}
```

## Targeters

### GroundLevelTargeter

```
@TGL{y=;}
```

### LocationPredictingTargeter

```
@TPL{time=; y=; ignoreY=; ignoreIfStill=;}
```

### ConnectedBlocksTargeter

```
@TCB{loc=; locOffset=; exclude=; depth=;}
```

### EntityInSightTargeter

```
@TEIS{maxDistance=;}
```

### EntityByClassInRadiusTargeter

This allows you to return any entities in radius that match a bukkit interface, the interface MUST extend the bukkit's Entity interface

```
@TEIR{class=;radius=;}
```

## Conditions

### StringContainsCondition

```
?stringcontains{s=;c=;}
```

### OxygenLevelCondition

```
?stringcontains{air=;}
```

### YLevelCondition

```
?isylevel{y=}
```

## Signals

### SkillCastSignal

```
~onSkillCast, the variable <caster.var.skillname> will return the internal name of the skill used. @trigger returns the player who triggered the event.
```

### PlayerChangeSlotSignal

```
~onPlayerChangeSlot, the variableS <previousSlot> and <nextSlot> can be used @trigger returns the player who triggered the event.
```

## Factions

Factions are used to tell when to apply extra damage to the mob. For example if the mob is in the faction "boss" then extra damage will be applied based on a placeholder such as %player_stat_boss_damage_bonus%. A mob can be in multiple factions, if the faction setting is for example boss,fire both bonuses will be applied.

### IsInFactionCondition

Checks if the boss is in a faction taking into account that it could be in multiple seperated by comma
```
?isinfaction{faction=}
```

## Placeholders

A placeholder to get a random color between 2 hex values was added. Requires PlaceholderAPI %alchemist_color_#hex1_hex2%, returns a random color between the hex values 1 and 2
