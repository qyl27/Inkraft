// title: engine_functions_include.ink
// author: qyl27
// license: CC0
// version: 2024.12.24

EXTERNAL isDebug()

EXTERNAL isInFlow(name)
EXTERNAL isInDefaultFlow()
EXTERNAL flowTo(name)
EXTERNAL flowToDefault()
EXTERNAL newFlow(name, flow)
EXTERNAL removeFlow(name)

EXTERNAL isEnded()
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

EXTERNAL getPlayerName()
EXTERNAL getWorldDayTime(worldId)
EXTERNAL getWorldGameTime(worldId)
EXTERNAL getWorldDay(worldId)
EXTERNAL getRealTime(pattern)

EXTERNAL runCommand(command)
EXTERNAL runUnlimitedCommand(command)
EXTERNAL runServerCommand(command)

EXTERNAL hasItem(itemId, count, nbtPath, nbtValue)
EXTERNAL countItem(itemId, nbtPath, nbtValue)
EXTERNAL giveItem(itemId, count, nbtPath, nbtValue)
EXTERNAL takeItem(itemId, count, nbtPath, nbtValue)
