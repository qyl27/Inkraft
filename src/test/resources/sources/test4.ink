INCLUDE engine_functions_include.ink

买吗？（2钻石换1苹果）
* [是] -> buy
* [算了] -> END

=== buy ===
~ temp has = hasItem("minecraft:diamond", 2, "", "")

变量 has： {has}

{ has:
    你有2钻石。
    ~ temp took = takeItem("minecraft:diamond", 2, "", "")
    变量 took： {took}
    { took:
        拿走了2钻石。
        ~ giveItem("minecraft:apple", 1, "", "")
        给了苹果。
        -> DONE
    - else:
        失败！拿不走2钻石。
        -> DONE
    }
- else:
    失败！你没有2钻石。
    -> DONE
}
