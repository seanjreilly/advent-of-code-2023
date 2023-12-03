package day03

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day03Test {
    private val sampleInput = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """.trimIndent().lines()

    @Test
    fun `findPartNumbers() should return the number runs that are adjacent to a symbol, given a line, line number and a map of symbol positions`() {
        val symbolPositions = sampleInput.buildSymbolPositions()

        assert(findPartNumbers(sampleInput[0], 0, symbolPositions) == listOf("467"))
        assert(findPartNumbers(sampleInput[1], 1, symbolPositions) == emptyList<String>())
        assert(findPartNumbers(sampleInput[2], 2, symbolPositions) == listOf("35", "633"))
        assert(findPartNumbers(sampleInput[3], 3, symbolPositions) == emptyList<String>())
        assert(findPartNumbers(sampleInput[4], 4, symbolPositions) == listOf("617"))
        assert(findPartNumbers(sampleInput[5], 5, symbolPositions) == emptyList<String>())
        assert(findPartNumbers(sampleInput[6], 6, symbolPositions) == listOf("592"))
        assert(findPartNumbers(sampleInput[7], 7, symbolPositions) == listOf("755"))
        assert(findPartNumbers(sampleInput[8], 8, symbolPositions) == emptyList<String>())
        assert(findPartNumbers(sampleInput[9], 9, symbolPositions) == listOf("664", "598"))
    }

    @Test
    fun `part1 should return the sum of all the part numbers in the engine schematic `() {
        assert(part1(sampleInput) == 4361L)
    }
    
    @Nested
    inner class StringTest {
        @Test
        fun `symbolPositions() should return the set of positions in the string that contain symbols`() {
            assert(sampleInput[0].symbolPositions() == emptySet<Int>())
            assert(sampleInput[1].symbolPositions() == setOf(3))

            assert(sampleInput[3].symbolPositions() == setOf(6))
            assert(sampleInput[8].symbolPositions() == setOf(3,5))
        }
    }

    @Nested
    inner class ListOfStringTest {
        @Test
        fun `buildSymbolPositions() should build a map of line number to symbol position`() {
            val result: Map<Int, Set<Int>> = sampleInput.buildSymbolPositions()

            assert(result.size == sampleInput.size)
            sampleInput.forEachIndexed { index, line ->
                assert(result[index] == line.symbolPositions())
            }
        }
    }

    @Nested
    inner class CharacterTest {
        @Test
        fun `isSymbol() should return false for any digit`() {
            assert(!'0'.isSymbol())
            assert(!'1'.isSymbol())
            assert(!'2'.isSymbol())
            assert(!'3'.isSymbol())
            assert(!'4'.isSymbol())
            assert(!'5'.isSymbol())
            assert(!'6'.isSymbol())
            assert(!'7'.isSymbol())
            assert(!'8'.isSymbol())
            assert(!'9'.isSymbol())
        }

        @Test
        fun `isSymbol() should return false for a dot character`() {
            assert(!'.'.isSymbol())
        }

        @Test
        fun `isSymbol() should return true for all other symbol characters`() {
            assert('*'.isSymbol())
            assert('#'.isSymbol())
            assert('-'.isSymbol())
            assert('+'.isSymbol())
            assert('='.isSymbol())
            assert('%'.isSymbol())
            assert('@'.isSymbol())
            assert('/'.isSymbol())
            assert('$'.isSymbol())
            assert('&'.isSymbol())
        }
    }
}
