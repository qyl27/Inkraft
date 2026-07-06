INCLUDE engine_functions_include.ink

Inkraft 测试剧本六，JSON 数组和映射：

~ temp array = createArray()
新数组：{array}

~ array = arrayAdd(array, 1)
~ array = arrayAdd(array, 2.0)
~ array = arrayAdd(array, true)
~ array = arrayAdd(array, "1")
arrayAdd：{array}    // Expected: [1, 2.0, true, "1"]

~ array = arraySet(array, 1, 2.3)
~ array = arrayRemove(array, 3)
arraySet & arrayRemove: {array} // Expected: [1, 2.3, true]

isArray：{isArray(array)}   // Expected: true
isArrayEmpty：{isArrayEmpty(array)} // Expected: true
arraySize：{arraySize(array)}   // Expected: 3
arrayGet：{arrayGet(array, 1)}  // Expected: 2.3
arrayContains：{arrayContains(array, 1)}    // Expected: true
arrayFirst：{arrayFirst(array)} // Expected: 1
arrayLast：{arrayLast(array)}   // Expected: true
arrayHas: {arrayHas(array, 3)}  // Expected: false

isArray("[3, 2, 1]"): {isArray("[3, 2, 1]")}    // Expected: true

~ temp inner = createArray()
~ inner = arrayAdd(inner, "wolf")
~ inner = arrayAdd(inner, "seikou!")
~ array = arrayAdd(array, inner)
nesting arrayAdd：{array}
nesting arrayGet：{arrayGet(array, 3)}  // Expected: "["wolf", "seikou!"]"

~ temp map = createMap()
新字典：{map}
~ map = mapSet(map, "b", 2.0)
~ map = mapSet(map, "a", "value")
mapSet：{map}   // Expected: {"b": 2.0, "a": "value"}, order is not important

map is not an array：{isArray(map)} // Expected: false
array is not a map：{isMap(array)}  // Expected: false

isMap：{isMap(map)} // Expected: true
mapSize：{mapSize(map)} // Expected: 2
mapGet：{mapGet(map, "a")}  // Expected: "value"

~ map = mapRemove(map, "b")
mapRemove：{map}    // Expected: {"a": "value"}
mapContains：{mapContains(map, "b")}    // Expected: false

~ map = mapSet(map, "c", inner)
nesting mapSet：{map}   // Expected: {"a": "value", "c": "["wolf", "seikou!"]"}, order is not important
nesting mapGet：{mapGet(map, "c")}  // Expected: "["wolf", "seikou!"]"

