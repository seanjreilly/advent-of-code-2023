package day13

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.readInput
import kotlin.IllegalArgumentException

class Day13Test {
    private val sampleInput = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent().lines()

    @Test
    fun `part1 should find the reflection in each pattern and return the sum of row indicies times 100 plus column indices`() {
        assert(part1(sampleInput) == 405L)
    }
    
    @Test
    fun `findMirrorRow should look from top to bottom for matching rows`() {
        val input = """
            aaa
            aaa
            bbb
        """.trimIndent().lines()

        assert(findMirrorRow(input) == 1)
    }

    @Test
    fun `findMirrorRow should also look from bottom to top for matching rows`() {
        val input = """
            ###
            ##.
            ##.
        """.trimIndent().lines()

        assert(findMirrorRow(input) == 2)
    }

    @Test
    fun `findMirrorRow should return null when there is no fold, even if it looks like there could be one`() {
        val input = """
            ##.
            ###
            ...
            ##.
        """.trimIndent().lines()

        assert(findMirrorRow(input) == null)
    }

    @Test
    fun `findMirrorRow should return the correct index for a reflection in the middle of the pattern`() {
        val input = """
            ...
            ..#
            .#.
            .#.
            ..#
            ...
        """.trimIndent().lines()

        assert(findMirrorRow(input) == 3)
    }

    @Test
    fun `findMirrorRow should consider all potential fold points given a pattern with more than one`() {
        val input = """
            ..###..##..
            .#.#.....##
            #.##..##.#.
            #.#..###..#
            .#..##.##.#
            .#....##..#
            #.####.##.#
            #.####.##.#
            .#....###.#
            .#..##.##.#
            #.#..###..#
            #.##..##.#.
            .#.#.....##
            ..###..##..
            ..###..##..
        """.trimIndent().lines()

        assert(findMirrorRow(input) == 14)
    }

    @Test
    fun `findMirrorRow should work with sample input`() {
        val chunkedInput = sampleInput.chunkOnPredicate { it.isBlank() }
        assert(findMirrorRow(chunkedInput.last()) == 4)
    }
    
    @Test
    fun `oneCharAwayFromEqual should return true given two lines that differ by exactly one character`() {
        val a = "###.###".customHashCode()
        val b = "#######".customHashCode()
        
        assert(a.oneCharAwayFromEqual(b))
        assert(b.oneCharAwayFromEqual(a))
    }

    @Test
    fun `oneCharAwayFromEqual should return false given two equal lines`() {
        val a = "#.#.#.#".customHashCode()
        assert (!a.oneCharAwayFromEqual(a))
    }

    @Test
    fun `oneCharAwayFromEqual should return false given two lines that differ by more than one character`() {
        val a = ".......".customHashCode()
        val b = "..#.#..".customHashCode()

        assert(!a.oneCharAwayFromEqual(b))
        assert(!b.oneCharAwayFromEqual(a))
    }

    @Test
    fun `findReflection should return a row mirror orientation given a horizontal reflection`() {
        val chunkedInput = sampleInput.chunkOnPredicate { it.isBlank() }
        val result: ReflectionResult = findReflection(chunkedInput.last())
        assert(result.mirrorOrientation == MirrorOrientation.Row)
        assert(result.index == 4)
    }

    @Test
    fun `findReflection should return a column mirror orientation given a vertical reflection`() {
        val chunkedInput = sampleInput.chunkOnPredicate { it.isBlank() }
        val result = findReflection(chunkedInput.first())

        assert(result.mirrorOrientation == MirrorOrientation.Column)
        assert(result.index == 5)
    }

    @Test
    fun `chunkOnPredicate should split a list based on a predicate`() {
        val result = sampleInput.chunkOnPredicate { it.isBlank() }
        assert(result.size == 2)
        assert(result[0] == sampleInput.slice(0 until 7))
        assert(result[1] == sampleInput.slice(8 .. 14))
    }

    @Test
    fun `transpose should turn rows into columns and columns into rows given a rectangular list of strings`() {
        val input = """
            abc
            123
        """.trimIndent().lines()

        val expectedResult = """
            a1
            b2
            c3
        """.trimIndent().lines()

        assert(input.transpose() == expectedResult)
    }

    @Test
    fun `transpose should throw an exception given a non-rectangular list of strings`() {
        val input = """
            a
            ab
            abc
        """.trimIndent().lines()

        val exception = assertThrows<IllegalArgumentException> { input.transpose() }
        assert(exception.message == "every string in the list must have the same length")
    }

    @Test
    fun `max line length should be less than 64`() {
        val maxLineLength = readInput("Day13").maxOf { it.length }
        println(maxLineLength)
        assert(maxLineLength < 32)
    }

    @Test
    fun `max chunk length should be less than 64`() {
        val maxChunkLength = readInput("Day13").chunkOnPredicate { it.isBlank() }.maxOf { it.size }
        println(maxChunkLength)
        assert(maxChunkLength < 32)
    }

    @Test
    fun `customHashCode should set a bit for each hash character`() {
        val a = ".#.#.#.#.#"
        val b = "...#.#.#.#"

        val aHash: UInt = a.customHashCode()
        val bHash: UInt = b.customHashCode()

        assert(aHash != bHash)
        assert(aHash.countOneBits() == 5)
        assert(bHash.countOneBits() == 4)
    }
}