INCLUDE engine_functions_include.ink

测试：
12金苹果换1金钻石
* [匹配名称和lore] -> buy_1
* [只匹配lore] -> buy_2

=== buy_1 ===
~ temp has = hasItem("minecraft:apple", 12, "components", "$<$qminecraft:custom_name$q:$d$<$qcolor$q:$qgold$q,$qitalic$q:false,$qtext$q:$q金苹果$q$>$d,$qminecraft:lore$q:[$d$<$qcolor$q:$qaqua$q,$qitalic$q:false,$qtext$q:$q名字是金色的字的苹果$q$>$d]$>")

变量 has： {has}

{ has:
    ~ temp took = takeItem("minecraft:apple", 12, "components", "$<$qminecraft:custom_name$q:$d$<$qcolor$q:$qgold$q,$qitalic$q:false,$qtext$q:$q金苹果$q$>$d,$qminecraft:lore$q:[$d$<$qcolor$q:$qaqua$q,$qitalic$q:false,$qtext$q:$q名字是金色的字的苹果$q$>$d]$>")
    变量 took： {took}
    { took:
        ~ temp gave = giveItem("minecraft:diamond", 1, "components", "$<$qminecraft:custom_name$q:$d$<$qcolor$q:$qgold$q,$qitalic$q:false,$qtext$q:$q金钻石$q$>$d,$qminecraft:lore$q:[$d$<$qcolor$q:$qaqua$q,$qitalic$q:false,$qtext$q:$qqyl给的钻石$q$>$d]$>")
        变量 gave： {gave}
        -> DONE
    - else:
        失败！
        -> DONE
    }
- else:
    失败！
    -> DONE
}

=== buy_2 ===
~ temp has = hasItem("minecraft:apple", 12, "components.minecraft:lore", "[$d$<$qcolor$q:$qaqua$q,$qitalic$q:false,$qtext$q:$q名字是金色的字的苹果$q$>$d]")

变量 has： {has}

{ has:
    ~ temp took = takeItem("minecraft:apple", 12, "components.minecraft:lore", "[$d$<$qcolor$q:$qaqua$q,$qitalic$q:false,$qtext$q:$q名字是金色的字的苹果$q$>$d]")
    变量 took： {took}
    { took:
        ~ temp gave = giveItem("minecraft:diamond", 1, "components.minecraft:lore", "[$d$<$qcolor$q:$qaqua$q,$qitalic$q:false,$qtext$q:$qqyl给的钻石$q$>$d]")
        变量 gave： {gave}
        -> DONE
    - else:
        失败！
        -> DONE
    }
- else:
    失败！
    -> DONE
}
