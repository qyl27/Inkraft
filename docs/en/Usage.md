# Usage

Other languages: [简体中文](../zh/使用方法.md)

## Basic Concepts

- **Engine**: Inkraft itself.
- **Story (story script)**: The smallest unit executed by the engine. It is a JSON file written in the Ink scripting language and compiled/exported from Ink.
- **Engine function**: A bridge between a story and the game. Engine functions can be called from story content.
- **System function**: An engine function that controls the engine itself.
- **Game function**: An engine function that interacts with Minecraft.
- **Engine variable**: A variable provided by the engine. It is independent from individual stories, can be shared across stories, and is stored on the player.

### Writing Stories

See:

- [Writing Story Scripts](Writing-Story-Scripts.md)
- [Language Enhancements](Language-Enhancements.md)

## Adding Stories

Put exported JSON files ending with `.ink.json` into the `data/<namespace>/inkraft_story` folder of a data pack, then reload data packs.

The namespaced ID of a story is `<namespace>:<story path>`. For example, if a story file is located at `data/example/inkraft_story/npc1/talk.ink.json`, its namespaced ID is `example:npc1/talk`.

## Permissions

### NeoForge

- `inkraft.use`: The permission node for usage. The default permission level is 0.
- `inkraft.admin`: The permission node for administrators. The default permission level is 2.

### Fabric

- The usage permission level is 0.
- The administrator permission level is 2.

## Commands

The Inkraft engine can be interacted with in game through commands.

### Start

Start a new story.

Command: `/inkraft start <id> [player]`

`id`: The namespaced ID of the story.

`player`: A player, or a selector that selects exactly one player. This player is used as the command target. This argument is available only with administrator permission.

### Resume

Continue a running story, or show the last dialogue line and options again.

Command: `/inkraft current [player]`

`player`: A player, or a selector that selects exactly one player. This player is used as the command target. This argument is available only with administrator permission.

### Continue

Choose an option or click continue.

Command: `/inkraft next <token> [optionIndex]` or `/inkraft next <player> [optionIndex]`

`token`: A temporary value generated to prevent players from clicking expired options. Under normal circumstances, it is not shown to players and cannot be obtained manually.

`player`: A player, or a selector that selects exactly one player. This player is used as the command target. This argument is available only with administrator permission.

`optionIndex`: The option index, starting from 0. It is not required when there are no options.

### Reset (Administrator)

Reset the engine state. This deletes all information, including the running story state and all stored engine variables. This command is available only with administrator permission.

Command: `/inkraft reset [player]`

`player`: A player, or a selector that selects exactly one player. This player is used as the command target. This argument is available only with administrator permission.

### Get an Engine Variable (Administrator)

Command: `/inkraft variables get <name|{<player> <name>}>`

`name`: The engine variable name. It can be any string.

`player`: A player, or a selector that selects exactly one player. This player is used as the command target.

### Set an Engine Variable (Administrator)

Command: `/inkraft variables set <name|{<player> <name>}> <value>`

`name`: The engine variable name. It can be any string.

`player`: A player, or a selector that selects exactly one player. This player is used as the command target.

`value`: The engine variable value. It can be any string.

### View the Engine Version

This is useful when reporting issues.

Command: `/inkraft version`

## Predicates

Inkraft provides the loot table condition `inkraft:story`, which checks whether a player is running a story. It accepts an optional string parameter named `story` for the story namespaced ID. If omitted, it matches any story.

Inkraft also provides the predicate `inkraft:in_any_story`, which checks whether a player is running any story.

For how to write predicates, see [Predicate - Minecraft Wiki](https://minecraft.wiki/w/Predicate).
