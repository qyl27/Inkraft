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

Use `EXTERNAL <function definition>` at the beginning of an Ink script to declare external functions. The engine functions provided by Inkraft are divided into system functions and game functions. System functions are related to Ink script execution, while game functions operate on Minecraft game content.

Appendix A provides a declaration file containing all engine functions. It can be used for editor syntax hints and included directly with `INCLUDE` when writing scripts.

Function names in the Inkraft engine generally use camelCase.

When a function parameter is described as "nullable", it means an empty string `""` can be used as a placeholder. This is a workaround because Ink does not support function overloading or have a type representing an empty value.

### System Functions

| Function definition | Description                                                      | Parameters | Return value                            |
|---------------------|------------------------------------------------------------------|------------|-----------------------------------------|
| isDebug()           | Indicates whether the currently running engine is in Debug mode. |            | bool, true means Debug mode is enabled. |

#### Parallel Flow Functions

| Function definition | Description                                                  | Parameters                                                                                    | Return value              |
|---------------------|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------|---------------------------|
| isInFlow(name)      | Checks whether the player is in the specified parallel flow. | name: The parallel flow name string.                                                          | bool, true means yes.     |
| isInDefaultFlow()   | Checks whether the player is in the default parallel flow.   |                                                                                               | bool, true means yes.     |
| flowTo(name)        | Switches to the specified parallel flow.                     | name: The parallel flow name string.                                                          | bool, true means success. |
| flowToDefault()     | Switches to the default parallel flow.                       |                                                                                               | bool, true means success. |
| newFlow(name, flow) | Creates the specified parallel flow and switches to it.      | name: The parallel flow name string.<br />flow: The story knot executed by the parallel flow. | bool, true means success. |
| removeFlow(name)    | Removes the specified parallel flow.                         | name: The parallel flow name string.                                                          | bool, true means success. |

#### Story Line Functions

| Function definition | Description                                                                                                                                                            | Parameters                                                                                                                                                                                                               | Return value                                    |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| isEnded()           | Checks whether the current story has any more lines available, that is, whether it has ended. Story lines in different parallel flows are independent from each other. |                                                                                                                                                                                                                          | bool, true means there are no more story lines. |
| pause()             | Pauses story playback and lets the player click to continue manually.                                                                                                  |                                                                                                                                                                                                                          |                                                 |
| setLineTicks(ticks) | Sets the story playback speed.<br />(In practice, this sets the value of an engine variable named `line_pause_ticks`.)                                                 | ticks: The playback speed, meaning the wait time after each line is displayed, in game ticks. It must be a positive integer, 0, or the special value -1, which means the player must click to continue after every line. | bool, true means success.                       |
| unsetLineTicks()    | Clears the story playback speed setting.                                                                                                                               |                                                                                                                                                                                                                          |                                                 |

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
