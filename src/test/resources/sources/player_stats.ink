INCLUDE includes/engine_functions_include.ink

验证玩家统计函数。
* [运行] -> verify

=== verify ===
~ temp pickedUpBefore = getPlayerStat("picked_up", "diamond")
~ temp damageTakenBefore = getPlayerStat("minecraft:custom", "minecraft:damage_taken")
~ temp formattedDamageTakenBefore = getFormattedPlayerStat("minecraft:custom", "minecraft:damage_taken")
~ temp playTime = getPlayerStat("minecraft:custom", "minecraft:play_time")

~ temp gameModeResult = runSilentUnlimitedCommand("gamemode survival @s")
~ temp damageResult = runSilentUnlimitedCommand("damage @s 13 minecraft:generic")
~ temp summonResult = runSilentUnlimitedCommand("summon minecraft:item ~ ~ ~ \{Tags:['inkraft_player_stat_test'],Item:\{id:'minecraft:diamond',count:7\}\}")
~ temp ownerResult = runSilentUnlimitedCommand("data modify entity @e[type=minecraft:item,tag=inkraft_player_stat_test,sort=nearest,limit=1] Owner set from entity @s UUID")
PLAYER_STAT_WAIT_FOR_PICKUP

~ temp pickedUpAfter = getPlayerStat("picked_up", "diamond")
~ temp damageTakenAfter = getPlayerStat("minecraft:custom", "minecraft:damage_taken")
~ temp formattedDamageTakenAfter = getFormattedPlayerStat("minecraft:custom", "minecraft:damage_taken")
~ temp missingType = getPlayerStat("testmod:missing", "minecraft:stone")
~ temp missingValue = getPlayerStat("minecraft:mined", "testmod:missing")
~ temp malformedType = getPlayerStat("bad id", "minecraft:stone")
~ temp formattedMissing = getFormattedPlayerStat("minecraft:mined", "testmod:missing")

// Blade Ink 1.3.2 compares boxed integers by reference for ==, so use bounds for an exact value above 127.
{ pickedUpBefore == 0 and damageTakenBefore == 0 and formattedDamageTakenBefore == "0.00" and damageResult == 1 and summonResult == 1 and ownerResult == 1 and pickedUpAfter == 7 and damageTakenAfter >= 130 and damageTakenAfter <= 130 and formattedDamageTakenAfter == "13.00" and missingType == false and missingValue == false and malformedType == false and formattedMissing == false:
    PLAYER_STAT_OK
- else:
    PLAYER_STAT_FAIL pickedUpBefore={pickedUpBefore}, damageTakenBefore={damageTakenBefore}, formattedDamageTakenBefore={formattedDamageTakenBefore}, playTime={playTime}, gameModeResult={gameModeResult}, damageResult={damageResult}, summonResult={summonResult}, ownerResult={ownerResult}, pickedUpAfter={pickedUpAfter}, damageTakenAfter={damageTakenAfter}, formattedDamageTakenAfter={formattedDamageTakenAfter}, missingType={missingType}, missingValue={missingValue}, malformedType={malformedType}, formattedMissing={formattedMissing}
}
-> END
