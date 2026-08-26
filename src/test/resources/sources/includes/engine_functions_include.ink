// title: engine_functions_include.ink
// author: qyl27
// license: CC0
// version: 2026.08.26


// System functions

EXTERNAL isDebug()

EXTERNAL isInFlow(name)
EXTERNAL hasFlow(name)
EXTERNAL isFlowEnded(name)
EXTERNAL flowTo(name)
EXTERNAL newFlow(name, knot)
EXTERNAL removeFlow(name)

=== function isInDefaultFlow() ===
~ return isInFlow("DEFAULT_FLOW")

=== function flowToDefault() ===
~ return flowTo("DEFAULT_FLOW")


EXTERNAL pause()
EXTERNAL setLineTicks(ticks)
EXTERNAL unsetLineTicks()

EXTERNAL hasVariable(name)
EXTERNAL getVariable(name)
EXTERNAL setVariable(name, value)
EXTERNAL unsetVariable(name)
EXTERNAL clearVariables()

EXTERNAL logDebug(message)
EXTERNAL logInfo(message)
EXTERNAL logWarn(message)
EXTERNAL logError(message)

EXTERNAL parseBool(str)
EXTERNAL parseInt(str)
EXTERNAL parseFloat(str)
EXTERNAL toString(value)


// Game functions

EXTERNAL getPlayerName()
EXTERNAL getPlayerStat(typeId, valueId)
EXTERNAL getFormattedPlayerStat(typeId, valueId)
EXTERNAL getWorldDayTime(worldId)
EXTERNAL getWorldGameTime(worldId)
EXTERNAL getWorldDay(worldId)
EXTERNAL getRealTime(pattern)

EXTERNAL runCommand(command)
EXTERNAL runUnlimitedCommand(command)
EXTERNAL runSilentUnlimitedCommand(command)
EXTERNAL runServerCommand(command)

EXTERNAL getScoreboard(objective)
EXTERNAL setScoreboard(objective, value)
EXTERNAL addScoreboard(objective, value)
EXTERNAL subScoreboard(objective, value)
EXTERNAL multiplyScoreboard(objective, value)

EXTERNAL getStorage(id, nbtPath)
EXTERNAL setStorage(id, nbtPath, value)

EXTERNAL hasItem(itemId, count, nbtPath, nbtValue)
EXTERNAL countItem(itemId, nbtPath, nbtValue)
EXTERNAL giveItem(itemId, count, nbtPath, nbtValue)
EXTERNAL takeItem(itemId, count, nbtPath, nbtValue)


// Language enhancement functions

EXTERNAL randomUuid()
EXTERNAL isUuid(value)

EXTERNAL createArray()
EXTERNAL isArray(value)
EXTERNAL arraySize(array)
EXTERNAL arraySet(array, index, value)
EXTERNAL arrayGet(array, index)
EXTERNAL arrayAdd(array, value)
EXTERNAL arrayRemove(array, index)
EXTERNAL arrayContains(array, element)

EXTERNAL createMap()
EXTERNAL isMap(value)
EXTERNAL mapSize(map)
EXTERNAL mapSet(map, key, value)
EXTERNAL mapGet(map, key)
EXTERNAL mapRemove(map, key)
EXTERNAL mapContains(map, key)

=== function arrayHas(array, index) ===
~ return index >= 0 and index < arraySize(array)

=== function isArrayEmpty(array) ===
~ return arraySize(array) == 0

=== function arrayFirst(array) ===
{ arrayHas(array, 0):
    ~ return arrayGet(array, 0)
}
~ return false

=== function arrayLast(array) ===
~ temp size = arraySize(array)
{ size > 0:
    ~ return arrayGet(array, size - 1)
}
~ return false

=== function isMapEmpty(map) ===
~ return mapSize(map) == 0
