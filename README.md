# OreDetectors Mod by FreneticFeline

OreDetectors Mod is a Minecraft mod that provides new items for detecting ore blocks.

## Installation Requirements
OreDetectors Mod versions 1.x-y.z require Minecraft 1.x with compatible version
of MinecraftForge installed.

## Installation Instructions
Copy the `mod_ffOreDetectors_forge-1.x-y.z.jar` file to the `mods` directory in your Minecraft
data directory.

## Usage Instructions
An ore detector, when anywhere in the inventory, will periodically beep whenever there
is a block of the particular ore type nearby.  When held, the ore detector will beep
faster when facing in the direction of the closest ore block, and louder the closer
the block is to the player.  "Using" the item (holding right-click) will activate long
range detection mode, until the player stops using the detector.  Long-range detection
will damage the detector.

### Supported Ore Types
Detectors can be built for the following types of ore.

- Diamond
- Emerald
- Redstone
- Gold
- Lapis
- Iron
- Quartz 

### Detector Recipe
The following recipe is used for each type of ore detector. 'C' represents a compass,
'R' a redstone repeater, 'I' a block of iron.  '0' is one of diamond, emerald, redstone,
gold ingot, lapis block, iron ingot, or nether quartz.

    _C_
    RIR
    _O_

## Development Setup Instructions
Follow the standard Forge mod development setup instructions.  They can be found
in the file named `README-MinecraftForge.txt`

