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
    fun `findPartNumbers() should return the part numbers that are adjacent to at least one symbol`() {
        val partNumbers: List<PartNumber> = findPartNumbers(sampleInput)

        assert(partNumbers.size == 8)
        assert(partNumbers.contains(PartNumber("467", SymbolPosition(1,3))))
        assert(partNumbers.contains(PartNumber("35", SymbolPosition(1,3))))
        assert(partNumbers.contains(PartNumber("633", SymbolPosition(3,6))))
        assert(partNumbers.contains(PartNumber("617", SymbolPosition(4,3))))
        assert(partNumbers.contains(PartNumber("592", SymbolPosition(5,5))))
        assert(partNumbers.contains(PartNumber("755", SymbolPosition(8,5))))
        assert(partNumbers.contains(PartNumber("664", SymbolPosition(8,3))))
        assert(partNumbers.contains(PartNumber("598", SymbolPosition(8,5))))

    }

    private infix fun <T> List<T>.equalsIgnoreOrder(other:List<T>) : Boolean {
        return this.size == other.size && this.toSet() == other.toSet()
    }

    @Test
    fun `findGears should find star symbols adjacent to exactly 2 part numbers and return the values`() {
        val results: List<Gear> = findGears(sampleInput)

        assert(results.size == 2)
        assert(results[0].partNumbers equalsIgnoreOrder listOf(467L, 35L))
        assert(results[1].partNumbers equalsIgnoreOrder listOf(755L, 598L))
    }

    @Test
    fun `part1 should return the sum of all the part numbers in the engine schematic `() {
        assert(part1(sampleInput) == 4361L)
    }

    @Test
    fun `part2 should calculate the product of the part numbers for each gear and return the sum of those products`() {
        assert(part2(sampleInput) == 467835L)
    }

    @Nested
    inner class ListOfStringTest {

        @Test
        fun `buildSymbolPositions() should build a set of SymbolPositions in the input that correspond to symbols`() {
            val result: Set<SymbolPosition> = sampleInput.buildSymbolPositions()

            assert(result.size == 6)
            assert(SymbolPosition(1,3) in result)
            assert(SymbolPosition(3,6) in result)
            assert(SymbolPosition(4,3) in result)
            assert(SymbolPosition(5,5) in result)
            assert(SymbolPosition(8,3) in result)
            assert(SymbolPosition(8,5) in result)
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
