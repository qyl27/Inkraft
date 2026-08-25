INCLUDE includes/engine_functions_include.ink

测试：
12金苹果换1金钻石
* [匹配名称和lore] -> buy_1
* [只匹配lore] -> buy_2
* [只匹配名称] -> buy_3

// /give @s apple[minecraft:custom_name={"color":"gold","italic":false,"text":"金苹果"},minecraft:lore=[{"color":"aqua","italic":false,"text":"名字是金色的字的苹果"}]]
// /give @s apple[minecraft:lore=[{"color":"aqua","italic":false,"text":"名字是金色的字的苹果"}]]
// /give @s apple[minecraft:custom_name={"color":"gold","italic":false,"text":"金苹果"}]

=== function take(nbt) ===
~ temp has = hasItem("minecraft:apple", 12, "components", nbt)
变量 has： {has}

{ has:
    ~ temp took = takeItem("minecraft:apple", 12, "components", nbt)
    变量 took： {took}
    { took:
        ~ temp result = "\{\"minecraft:custom_name\":\{\"color\":\"gold\",\"italic\":false,\"text\":\"金钻石\"\},\"minecraft:lore\":[\{\"color\":\"aqua\",\"italic\":false,\"text\":\"qyl给的钻石\"\}]\}"
        ~ temp gave = giveItem("minecraft:diamond", 1, "components", result)
        变量 gave： {gave}
        ~ return
    - else:
        takeItem 失败！
        ~ return
    }
- else:
    hasItem 失败！
        ~ return
}

=== buy_1 ===
~ temp nbt = "\{\"minecraft:custom_name\":\{\"color\":\"gold\",\"italic\":0b,\"text\":\"金苹果\"\},\"minecraft:lore\":[\{\"color\":\"aqua\",\"italic\":0b,\"text\":\"名字是金色的字的苹果\"\}]\}"
~ take(nbt)
-> DONE

=== buy_2 ===
~ temp nbt = "\{\"minecraft:lore\":[\{\"color\":\"aqua\",\"italic\":0b,\"text\":\"名字是金色的字的苹果\"\}]\}"
~ take(nbt)
-> DONE


=== buy_3 ===
~ temp nbt = "\{\"minecraft:custom_name\":\{\"color\":\"gold\",\"italic\":0b,\"text\":\"金苹果\"\}\}"
~ take(nbt)
-> DONE
