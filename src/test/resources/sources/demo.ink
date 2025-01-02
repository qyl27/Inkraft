INCLUDE engine_functions_include.ink

你好 {getPlayerName()} ，

VAR variable_name = "inkraft_demo_programmer"
欢迎来到 Inkraft 的演示章节。

VAR money = 10
VAR bun = 0
VAR melon = 0

现在请你出门买2个包子，如果看到卖西瓜的，买1个。包子和西瓜都是1个1金。
-> out_the_door

=== out_the_door
{现在你出门了，|现在你还在外面}口袋里有 {money} 金。{bun > 0: 手里拿着 {bun} 个包子。}{melon > 0: 手里拿着 {melon} 个西瓜。}你{|又}看见了卖西瓜的。
+ {money >= 1} [买1个包子]
    ~ money -= 1
    ~ bun += 1
    消费 1 金，包子 +1。
    -> out_the_door
+ {money > 0} [买1个西瓜]
    ~ money -= 1
    ~ melon += 1
    消费 1 金，西瓜 +1。
    -> out_the_door
+ {money < 10} 回家
    -> back 

=== back
你买了 {bun} 个包子，{melon} 个西瓜，还剩下 {money} 金。
{
- bun == 1 and melon == 0:
    唉，和你交流不了，典型的程序员思维（（（
    ~ setVariable(variable_name, true)
    {hasVariable(variable_name): 出门两回都看见买西瓜的，一次带回来一个包子，你故意找茬是不是？}
- bun == 2 and melon == 1:
    恭喜你通过了正常人类认证。
}
-> END
