INCLUDE test.common.ink

Inkraft 测试剧本二，系统函数：

一
二
三

接下来暂停，放慢速度说一遍！
~ setLineTicks(25)

一
二
三

~ unsetLineTicks()

设置一个全局变量~
初始值为1，每次调用增加1。
CONST varName = "testmod_testvalue"
~ temp hasNoVar = !hasVariable(varName)
{ hasNoVar:
    ~ setVariable(varName, 1)
- else: 
    ~ temp v = getVariable(varName)
    ~ setVariable(varName, v + 1)
}

现在变量值为 {getVariable(varName)} 。

