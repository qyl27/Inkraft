# Writing Story Scripts

Other languages: [简体中文](../zh/剧情脚本编写.md)

For Ink script syntax, see the [official tutorial](https://github.com/inkle/ink/blob/master/Documentation/WritingWithInk.md).

The rest of this document may refer to concepts from the Ink scripting language.

Ink itself supports the following data types: Bool, Int32, Float32, String, and List.

- Bool has the values true and false.
- When a number exceeds the range of Int32 or Float32, Ink silently discards the overflowing high-order data and preserves only the low-order data.
- Values of different data types can be implicitly promoted.
- Float32 does not include the special values +Infinity, -Infinity, sNaN, qNaN, +0.0, or -0.0 defined by IEEE 754:
  - ±Infinity is converted to ±3.4E38F.
  - sNaN, qNaN, +0.0, and -0.0 are all converted to +0.0.
- There is no type representing null or exceptions.
- List represents a choice from a finite set of values and is effectively an enumeration (Enum).

## Style Codes

Story text supports [MiniMessage](https://docs.advntr.dev/minimessage/format.html) to formatting. All standard MiniMessage tags are available, including colors, decorations, hover text, click events, gradients, and more.

## Engine Functions

This section describes the engine functions provided by Inkraft.

In Ink scripts, use `EXTERNAL <function definition>` to declare the primitive external functions provided by Inkraft. The include file in Appendix A contains both these external declarations and Ink convenience functions composed from the primitive functions. Engine functions are divided into system functions, which relate to Ink script execution, and game functions, which operate on Minecraft game content.

Scripts should include the Appendix A file with `INCLUDE engine_functions_include.ink`. Declaring the external functions manually does not provide the convenience functions implemented in Ink.

Function names in the Inkraft engine generally use camelCase.

When a function parameter is described as "nullable", it means an empty string `""` can be used as a placeholder. This is a workaround because Ink does not support function overloading or have a type representing an empty value.

### System Functions

| Function definition | Description                                                      | Parameters | Return value                            |
|---------------------|------------------------------------------------------------------|------------|-----------------------------------------|
| isDebug()           | Indicates whether the currently running engine is in Debug mode. |            | bool, true means Debug mode is enabled. |

#### Multiple Parallel Flow Control

**This is a beta feature of the Ink language.**

See: [Official documentation](https://github.com/inkle/ink/blob/master/Documentation/RunningYourInk.md#multiple-parallel-flows-beta)

Parallel flows store multiple independent context states within the same story script. Global variables and visit counts are shared between flows, while the current pointer, call stack, and similar state are isolated. Parallel flows can be used to implement story-level save/load behavior or interruptible conversations.

Flows are indexed by name. The default flow is named `DEFAULT_FLOW`; it is created when story playback begins, cannot be removed, and can therefore be treated as always existing in scripts.

The Flow-mutating functions `flowTo`, `newFlow`, and `removeFlow` send requests to the engine to switch parallel flows. These requests form a queue and execute after the current story line has been processed. Their return values only indicate whether the engine accepted the request, not whether it ultimately executed successfully. `isInFlow` and `hasFlow` only query committed Flow state and do not include pending requests.

| Function definition | Description | Parameters | Return value |
|---|---|---|---|
| isInFlow(name) | Checks whether the player is currently in the specified flow. | name: The parallel flow name. | bool, true means yes. |
| hasFlow(name) | Checks whether the current script state contains the specified flow. The default flow always exists. | name: The parallel flow name. | bool, true means it exists. |
| isFlowEnded(name) | Checks whether the specified flow has ended, meaning it has no subsequent story lines or choices. | name: The parallel flow name. | bool, true means ended. A missing flow is also considered ended. |
| flowTo(name) | Requests a switch to the specified flow, which must exist when the request executes. | name: The parallel flow name. | bool, true means the engine accepted the request; false means the request cannot currently be accepted. |
| newFlow(name, knot) | Requests creation of a flow from the specified knot and switches to it. If the target already exists, it is overwritten. | name: The parallel flow name.<br />knot: The knot name. | bool, true means the engine accepted the request; false means the request cannot currently be accepted. |
| removeFlow(name) | Requests removal of the specified non-default flow. Removing the current flow switches to the default flow after removal. | name: The parallel flow name. | bool, true means the engine accepted the request; false means the request cannot currently be accepted. |

The declaration file in Appendix A also provides common functions for working with the default flow.

| Function definition | Description | Return value |
|---|---|---|
| isInDefaultFlow() | Checks whether the player is currently in the default flow. | bool, true means yes. |
| flowToDefault() | Requests a switch to the default flow. | bool, true means the engine accepted the request. |

#### Story Line Functions

| Function definition | Description                                                                            | Parameters                                                                                                                                                                                                               | Return value              |
|---------------------|----------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------|
| pause()             | Pauses story playback and lets the player click to continue manually.                  |                                                                                                                                                                                                                          |                           |
| setLineTicks(ticks) | Sets the story playback speed.<br />(In practice, this sets the value of an engine variable named `line_pause_ticks`.) | ticks: The playback speed, meaning the wait time after each line is displayed, in game ticks. It must be a positive integer, 0, or the special value -1, which means the player must click to continue after every line. | bool, true means success. |
| unsetLineTicks()    | Clears the story playback speed setting.                                               |                                                                                                                                                                                                                          |                           |

#### Engine Variable Functions

Engine variables are attached to players and are used to persist data or pass it between different scripts.

Variable names are recommended to use snake_case. It is best to prefix them with the data pack namespace ID to avoid conflicts with other data packs.

If a variable needs to be shared between players, it is recommended to run server commands through game functions and use vanilla scoreboard or command storage mechanisms.

Engine variables store values as Bool, Int32, Float32, or String. Array and Map values are represented as String.

Because Ink's List type consists of enumeration values bound to the script being run, it is not meaningful outside that specific script. The engine therefore automatically converts a List value to String when it is written to an engine variable and returns it as String.

| Function definition      | Description                                             | Parameters                                               | Return value                                                                                       |
|--------------------------|---------------------------------------------------------|----------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| hasVariable(name)        | Checks whether an engine variable exists on the player. | name: The variable name.                                 | bool, true means it exists, false means it does not.                                               |
| getVariable(name)        | Gets an engine variable value.                          | name: The variable name.                                 | any, the engine variable value in its corresponding type.<br />Returns false if it does not exist. |
| setVariable(name, value) | Sets an engine variable value.                          | name: The variable name.<br />value: The variable value. | bool, true means success.                                                                          |
| unsetVariable(name)      | Removes an engine variable.                             | name: The variable name.                                 |                                                                                                    |
| clearVariables()         | Clears all engine variables.                            |                                                          |                                                                                                    |

#### Log Functions

Print logs to the console.

| Function definition | Description                 | Parameters            | Return value |
|---------------------|-----------------------------|-----------------------|--------------|
| logDebug(message)   | Prints a Debug-level log.   | message: The content. |              |
| logInfo(message)    | Prints an Info-level log.   | message: The content. |              |
| logWarn(message)    | Prints a Warning-level log. | message: The content. |              |
| logError(message)   | Prints an Error-level log.  | message: The content. |              |

#### Explicit Type Conversion

| Function definition | Description                   | Parameters        | Return value                                                              |
|---------------------|-------------------------------|-------------------|---------------------------------------------------------------------------|
| parseBool(str)      | Converts a string to Bool.    | str: The string.  | bool, only non-zero numeric strings or `"true"` can be converted to true. |
| parseInt(str)       | Converts a string to Int32.   | str: The string.  | int, 0 if conversion fails.                                               |
| parseFloat(str)     | Converts a string to Float32. | str: The string.  | float, 0 if conversion fails.                                             |
| toString(value)     | Converts a value to a string. | value: The value. | string, the string representation of the value.                           |

### Game Functions

| Function signature        | Description                           | Parameters                                                               | Return value                                                                   |
|---------------------------|---------------------------------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| getPlayerName()           | Gets the player name.                 |                                                                          | string, the player name.                                                       |
| getWorldDayTime(worldId)  | Gets the current day time of a world. | worldId: The world ID. Nullable, defaults to the player's current world. | int, 0-23999, the current day time.                                            |
| getWorldGameTime(worldId) | Gets the game time of a world.        | worldId: The world ID. Nullable, defaults to the player's current world. | int, game time.                                                                |
| getWorldDay(worldId)      | Gets the game day of a world.         | worldId: The world ID. Nullable, defaults to the player's current world. | int, game day.                                                                 |
| getRealTime(pattern)      | Gets the real-world system time.      | pattern: The date-time output format. Nullable, defaults to ISO-8601.    | int32\|float32\|string, the date-time output formatted with the given pattern. |

#### Player Statistics

The player-stat functions accept a statistic type ID and a statistic value ID to read a statistic value for the player bound to the current story script.

A statistic type ID is the ID of a `StatType`, such as `minecraft:custom` or `minecraft:mined`. A statistic value ID identifies an entry in the registry used by that `StatType`, such as `minecraft:bell_ring` for the Custom type. Tags are not supported as IDs here.

See [Statistics - Minecraft Wiki](https://minecraft.wiki/w/Statistics).


| Function signature                          | Description                          | Parameters                                                            | Return value                                                                                                                      |
|---------------------------------------------|--------------------------------------|-----------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| getPlayerStat(typeId, valueId)              | Gets the raw statistic value.        | typeId: The statistic type ID.<br />valueId: The statistic value ID. | int, the statistic value.<br />false if an ID is malformed or the statistic type or value does not exist.                         |
| getFormattedPlayerStat(typeId, valueId)     | Gets the formatted statistic string. | typeId: The statistic type ID.<br />valueId: The statistic value ID. | string, the value formatted by the statistic's `StatFormatter`.<br />false if an ID is malformed or the statistic type or value does not exist. |

An invalid statistic type ID or statistic value ID returns `false`; a valid ID with no recorded statistic has a raw value of `0`.

The formatting performed by `StatFormatter` is fixed, and the returned string is not localized.


#### Running Commands

| Function signature                 | Description                                                                                                               | Parameters                 | Return value                   |
|------------------------------------|---------------------------------------------------------------------------------------------------------------------------|----------------------------|--------------------------------|
| runCommand(command)                | Executes a command as the player.                                                                                         | command: The game command. | int, the command result value. |
| runUnlimitedCommand(command)       | Executes a command as the player with the highest permission level.                                                       | command: The game command. | int, the command result value. |
| runSilentUnlimitedCommand(command) | Executes a command as the player with the highest permission level, but does not report command output to the player.     | command: The game command. | int, the command result value. |
| runServerCommand(command)          | Executes a command as the server.<br />The player's world, position, and rotation are copied to the command source stack. | command: The game command. | int, the command result value. |

#### Scoreboard

| Function signature                   | Description                                   | Parameters                                            | Return value                                                            |
|--------------------------------------|-----------------------------------------------|-------------------------------------------------------|-------------------------------------------------------------------------|
| getScoreboard(objective)             | Gets the player's scoreboard value.           | objective: The objective name.                        | int, the scoreboard value.<br />false, if the objective does not exist. |
| setScoreboard(objective, value)      | Sets the player's scoreboard value.           | objective: The objective name.<br />value: The value. | true, success.<br />false, if the objective does not exist.             |
| addScoreboard(objective, value)      | Adds to the player's scoreboard value.        | objective: The objective name.<br />value: The value. | true, success.<br />false, if the objective does not exist.             |
| subScoreboard(objective, value)      | Subtracts from the player's scoreboard value. | objective: The objective name.<br />value: The value. | true, success.<br />false, if the objective does not exist.             |
| multiplyScoreboard(objective, value) | Multiplies the player's scoreboard value.     | objective: The objective name.<br />value: The value. | true, success.<br />false, if the objective does not exist.             |

#### Command Storage

| Function signature             | Description                     | Parameters                                                               | Return value                                                                                            |
|--------------------------------|---------------------------------|--------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| getStorage(id, nbtPath)        | Gets a command storage value.   | id: The namespace ID.<br />nbtPath: The NBT path.                        | int32\|float32\|string.<br />false if NBT path parsing fails. Error details are printed to the console. |
| setStorage(id, nbtPath, value) | Writes a command storage value. | id: The namespace ID.<br />nbtPath: The NBT path.<br />value: The value. | true, success.<br />false if NBT path or value parsing fails. Error details are printed to the console. |

#### Inventory

Note:

- Item tags all start with `#`.

| Function signature                         | Description                              | Parameters                                                                                                                                                                     | Return value                                                                                                                                                                   |
|--------------------------------------------|------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| hasItem(itemId, count, nbtPath, nbtValue)  | Checks whether the player has an item.   | itemId: The item ID or tag.<br />count: The count. Nullable, defaults to 1, integer, at least 1.<br />nbtPath: The NBT path. Nullable.<br />nbtValue: The NBT value. Nullable. | bool, true means yes, otherwise false.                                                                                                                                         |
| countItem(itemId, nbtPath, nbtValue)       | Gets the number of items the player has. | itemId: The item ID or tag.<br />nbtPath: The NBT path. Nullable.<br />nbtValue: The NBT value. Nullable.                                                                      | int, the count.                                                                                                                                                                |
| giveItem(itemId, count, nbtPath, nbtValue) | Gives an item to the player.             | itemId: The item ID.<br />count: The count. Nullable, defaults to 1, integer, at least 1.<br />nbtPath: The NBT path. Nullable.<br />nbtValue: The NBT value. Nullable.        |                                                                                                                                                                                |
| takeItem(itemId, count, nbtPath, nbtValue) | Takes an item from the player.           | itemId: The item ID or tag.<br />count: The count. Nullable, defaults to 1, integer, at least 1.<br />nbtPath: The NBT path. Nullable.<br />nbtValue: The NBT value. Nullable. | bool, true means success, otherwise false.<br />If the player has fewer matching items than requested, all matching items will still be taken, but the function returns false. |

### Function String Argument Escaping

Special characters in function string arguments need to be escaped. Ink also uses `\` for escaping.
The characters that need escaping are: `"`, `{`, `}`, `\`, `|`, and `#`. Standalone `<` and `>` do not need escaping. They only need to be escaped when they form sequences with Ink syntax meanings, such as `<>`, `->`, and `<-`, which should be written as `\<\>`, `\-\>`, and `\<\-`.

For example, when a function argument needs to pass an NBT string, it can be written as:

```ink
~ giveItem("minecraft:diamond", 1, "components", "\{\"minecraft:custom_name\":'\{\"text\":\"Gold Diamond\"\}'\}")
```

## Appendix
Appendix A [All Engine Function Declarations](../assets/engine_functions_include.ink)
