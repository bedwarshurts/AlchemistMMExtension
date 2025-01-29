
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
- sphereshape{particle=; radius=; count=; dirMultiplier=; shiftRadius=; variance=; direction=; speed=; skill=; delay=;}
```

### OpenChestMechanic

```
- openchest{action=;}
```

### LoopMechanic

```
- loop{condition=; skill=; delay=; loopID=; onStart=; onEnd=;}
```

### RingShapeMechanic

```
- ringshape{particle=; radius=; count=; dirMultiplier=; shiftRadius=; variance=; direction=; speed=; skill=; delay=; rotation=; rotMultiplier=;}
```

### PrimedTntMechanic

```
- primedtnt{fuse=; break=; damage=;}
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

## Conditions

### StringContainsCondition

```
?stringcontains{s=;c=;}
```

### YLevelCondition

```
?isylevel{y=}
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
