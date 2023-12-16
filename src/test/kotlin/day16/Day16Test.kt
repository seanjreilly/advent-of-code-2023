package day16

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day16Test {
    private val sampleInput = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent().lines()

    @Test
    fun `part1 should construct a contraption, energize the tiles, and return the number of energized tiles`() {
        assert(part1(sampleInput) == 46L)
    }
    
    @Nested
    inner class ContraptionTest {
        @Test
        fun `constructor should return a valid Contraption with appropriate bounds and tile data given input`() {
            val contraption = Contraption(sampleInput)

            assert(contraption.validXCoords == 0..9)
            assert(contraption.validYCoourds == 0..9)
            assert(contraption.tiles.map { it.joinToString("") }.toList() == sampleInput)
        }

        @Test
        fun `energizeTiles should follow a beam of light through the contraption and return the tiles it energizes`() {
            val rawExpectedEnergizedTiles = """
                ######....
                .#...#....
                .#...#####
                .#...##...
                .#...##...
                .#...##...
                .#..####..
                ########..
                .#######..
                .#...#.#..
            """.trimIndent().lines()

            val expectedEnergizedTiles = rawExpectedEnergizedTiles
                .flatMapIndexed { y, line ->
                    line.mapIndexed { x, char -> Point(x,y) to char }
                }
                .filter { it.second == '#' }
                .map { it.first }
                .toSet()

            val energizedTiles = Contraption(sampleInput).energizeTiles()
            assert(energizedTiles == expectedEnergizedTiles)
        }
    }
}