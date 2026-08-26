INCLUDE includes/engine_functions_include.ink

验证UUID函数。
* [运行] -> verify

=== verify ===
~ temp generated = randomUuid()
~ temp generatedValid = isUuid(generated)
~ temp canonicalUpper = isUuid("123E4567-E89B-12D3-A456-426614174000")
~ temp nil = isUuid("00000000-0000-0000-0000-000000000000")
~ temp shortened = isUuid("1-1-1-1-1")
~ temp empty = isUuid("")
~ temp whitespace = isUuid(" 123e4567-e89b-12d3-a456-426614174000")
~ temp braced = isUuid("\{123e4567-e89b-12d3-a456-426614174000\}")
~ temp malformed = isUuid("not-a-uuid")

{ generatedValid and canonicalUpper and nil and shortened and empty == false and whitespace == false and braced == false and malformed == false:
    UUID_FUNCTIONS_OK
- else:
    UUID_FUNCTIONS_FAIL generated={generated}, generatedValid={generatedValid}, canonicalUpper={canonicalUpper}, nil={nil}, shortened={shortened}, empty={empty}, whitespace={whitespace}, braced={braced}, malformed={malformed}
}
-> END
