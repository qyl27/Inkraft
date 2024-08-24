// title: engine_functions_include.ink
// author: qyl27
// license: CC0
// version: 2024.8.24

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
EXTERNAL runCommand(command)
EXTERNAL runUnlimitedCommand(command)
EXTERNAL runServerCommand(command)


=== function isDebug() ===
    ~ return true


=== function isInFlow(name) ===
    ~ return true
    
=== function isInDefaultFlow() ===
    ~ return true
    
=== function flowTo(name) ===
    ~ return true
    
=== function flowToDefault() ===
    ~ return true
    
=== function newFlow(name, flow) ===
    ~ return true
    
=== function removeFlow(name) ===
    ~ return true


=== function isEnded() ===
    ~ return false

=== function pause() ===
    ~ return true
    
=== function setLineTicks(ticks) ===
    ~ return true
	
=== function unsetLineTicks() ===
    ~ return true


=== function hasVariable(name) ===
    ~ return true

=== function getVariable(name) ===
    ~ return true

=== function setVariable(name, value) ===
    ~ return true
    
=== function unsetVariable(name) ===
    ~ return true
	
=== function clearVariables() ===
    ~ return true


=== function logDebug(message) ===
    ~ return true

=== function logInfo(message) ===
    ~ return true

=== function logWarn(message) ===
    ~ return true

=== function logError(message) ===
    ~ return true


=== function getPlayerName() ===
    ~ return "PlayerName"

=== function runCommand(command) ===
    ~ return 1

=== function runUnlimitedCommand(command) ===
    ~ return 1

=== function runServerCommand(command) ===
    ~ return 1

