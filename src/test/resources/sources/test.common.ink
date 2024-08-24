// title: engine_functions_include
// author: qyl27
// version: 2024.8.6

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

=== function isDebug() ===
    ~ return true


// Todo: parallel flow simulations.
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


// Todo: variable simulations.
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