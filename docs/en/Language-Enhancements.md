# Language Enhancements

Other languages: [简体中文](../zh/语言增强.md)

Inkraft extends the Ink language through external functions.

A lookahead-safe function does not produce side effects even if the Ink engine evaluates it ahead of time. Unless otherwise noted, all language enhancement functions are lookahead-safe.


## UUID

| Function definition | Description                                     | Return value                                    |
|---------------------|-------------------------------------------------|-------------------------------------------------|
| randomUuid()        | Generates a random UUID v4 string.              | string, a lowercase, hyphenated UUID v4 string. |
| isUuid(value)       | Checks whether a string can be parsed as a UUID. | bool; `false` if it cannot be parsed.           |

`randomUuid()` is not lookahead-safe; each call generates a new UUID.


## Data Structures

Inkraft uses JSON strings to emulate variable-length Array and dictionary-like Map data structures, such as `["element1", false, 12]` and `{"key1": "value1", "key2": 2.5}`.

Strings are immutable. Functions that modify a container return a new string, so the result must be assigned back to the original variable.

Because Ink supports fewer data types with much smaller ranges than JSON, the engine must normalize JSON strings:

- `null` is converted to `false`. A Map entry whose value is `null` is equivalent to one whose value is `false`.
- JSON Number values are converted to Ink Int32 or Float32 values. Data outside their ranges is discarded.
- When a nested JSON container is read, the inner container is recursively normalized and then converted to a JSON string. For example, `[[1.0]]` is normalized to `["[1.0]"]`.
- Inkraft processes JSON strings using Gson's strict JSON format:
  - Syntax such as comments and trailing commas is not supported.
  - JSON strings produced by function calls contain no whitespace.
  - Strict JSON has no `NaN` or `Infinity` literals. Special Float values produced by Ink operations are normalized according to Ink's numeric conversion rules.
- All conversions happen silently and do not produce log messages.

| Function definition                  | Description                                      | Return value                                                  |
|--------------------------------------|--------------------------------------------------|---------------------------------------------------------------|
| createArray()                        | Creates an empty Array.                          | string, `[]`.                                                 |
| isArray(str)                         | Checks whether a string is a valid Array.        | bool.                                                         |
| arraySize(array)                     | Gets the Array length.                           | int; -1 if the Array is invalid.                              |
| arraySet(array, index, value)        | Replaces the element at an index.                | string, the Array; false if the Array string is invalid.      |
| arrayGet(array, index)               | Gets the element at an index.                    | the element; false if the Array is invalid or out of bounds.  |
| arrayAdd(array, value)               | Appends a value.                                 | string, the Array; false if the Array string is invalid.      |
| arrayRemove(array, index)            | Removes an index and shifts later elements left. | string, the Array; false if the Array or index is invalid.    |
| arrayContains(array, element)        | Checks whether the Array contains an element.    | bool; false if the Array string is invalid.                   |

Map keys must be strings.

| Function definition          | Description                              | Return value                                                    |
|------------------------------|------------------------------------------|-----------------------------------------------------------------|
| createMap()                  | Creates an empty Map.                    | string, `{}`.                                                   |
| isMap(str)                   | Checks whether a string is a valid Map.  | bool.                                                           |
| mapSize(map)                 | Gets the number of keys in the Map.      | int; -1 if the Map string is invalid.                           |
| mapSet(map, key, value)      | Creates or replaces a key.               | string, the Map; false if the Map string is invalid.            |
| mapGet(map, key)             | Gets the value associated with a key.    | the value; false if the Map is invalid or the key is missing.   |
| mapRemove(map, key)          | Removes an existing key.                 | string, the Map; false if the Map string is invalid.            |
| mapContains(map, key)        | Checks whether the Map contains a key.   | bool; false if the Map string is invalid.                       |

The declaration file in Appendix A also provides functions composed from the primitive functions above.

| Function definition          | Description                                  | Return value                                                               |
|------------------------------|----------------------------------------------|----------------------------------------------------------------------------|
| arrayHas(array, index)       | Checks whether an Array contains an index.   | bool; false if the Array is invalid or does not contain the index.         |
| isArrayEmpty(array)          | Checks whether an Array is empty.            | bool; true if empty; false if invalid or non-empty.                         |
| arrayFirst(array)            | Gets the first element of an Array.          | the element; false if the Array is invalid or empty.                        |
| arrayLast(array)             | Gets the last element of an Array.           | the element; false if the Array is invalid or empty.                        |
| isMapEmpty(map)              | Checks whether a Map is empty.               | bool; true if empty; false if invalid or non-empty.                         |

Tip: Ink does not support exception handling, so an invalid Array or Map can only be reported by returning false.

## Appendix

Appendix A: [All Engine Function Declarations](../assets/engine_functions_include.ink)
