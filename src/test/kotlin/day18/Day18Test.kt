package day18

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.Point
import utils.readInput

class Day18Test {
    private val sampleInput = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent().lines()

    private val sampleInputBorderAndInterior = """
        #######
        #######
        #######
        ..#####
        ..#####
        #######
        #####..
        #######
        .######
        .######
    """.trimIndent().lines()

    @Test
    fun `part1 should parse a polygon from dig instructions and return the count of border points and points contained by the polygon`() {
        assert(part1(sampleInput) == 62L)
    }

    @Test
    fun `part2 should translate the hex codes, parse a polygon from dig instructions and return the count of border points and points contained by the polygon`() {
        assert(part2(sampleInput) == 952408144115L)
    }

    @Test
    fun `parsePolygon should convert the part 1 dig instructions into a list of vertices`() {
        val expectedPolygon = listOf(
            LongPoint(0,0),
            LongPoint(6, 0),
            LongPoint(6, 5),
            LongPoint(4, 5),
            LongPoint(4, 7),
            LongPoint(6, 7),
            LongPoint(6, 9),
            LongPoint(1, 9),
            LongPoint(1, 7),
            LongPoint(0, 7),
            LongPoint(0, 5),
            LongPoint(2, 5),
            LongPoint(2, 2),
            LongPoint(0, 2),
            LongPoint(0, 0),
        )

        val polygon = parsePolygon(sampleInput)

        assert(polygon == expectedPolygon)
    }

    @Test
    fun `parsePolygon should throw an exception when the polygon doesn't end where it begins`() {
        val badInstructions = sampleInput.dropLast(1)
        val exception: IllegalArgumentException = assertThrows { parsePolygon(badInstructions) }
        assert(exception.message == "Polygon must end at its start point")
    }

    @Test
    fun `parsePolygon should return a valid polygon given production input`() {
        val input = readInput("Day18")
        parsePolygon(input)
    }

    @Test
    fun `convertHexCodeToDigInstructions should translate the hex codes into dig instructions`() {
        val expectedInstructions = """
            R 461937
            D 56407
            R 356671
            D 863240
            R 367720
            D 266681
            L 577262
            U 829975
            L 112010
            D 829975
            L 491645
            U 686074
            L 5411
            U 500254
        """.trimIndent().lines()

        val instructions = convertHexCodeToDigInstructions(sampleInput)

        assert(instructions == expectedInstructions)
    }

    @Test
    fun `parsing production part 2 input should produce a valid polygon`() {
        val input = readInput("Day18")
        parsePolygon(convertHexCodeToDigInstructions(input))
    }

    @Test
    fun `countInteriorAndBorderPoints should return the number of points in the border and interior given a polygon`() {
        val expectedResult = parsePoints(sampleInputBorderAndInterior).size
        val polygon = parsePolygon(sampleInput)

        val result: Long = countInteriorAndBorderPoints(polygon)

        assert(result == expectedResult.toLong())
    }

    private fun parsePoints(input: List<String>): Set<Point> {
        return input
            .flatMapIndexed{ y, line -> line.mapIndexed { x, char -> Point(x,y) to char } }
            .filter { it.second == '#' }
            .map { it.first }
            .toSet()
    }
}