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

    private val sampleInputBorder = """
        #######
        #.....#
        ###...#
        ..#...#
        ..#...#
        ###.###
        #...#..
        ##..###
        .#....#
        .######
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
    fun `part1 should dig the border and return the count of border points and points contained by the border`() {
        assert(part1(sampleInput) == 62L)
    }

    @Test
    fun `digBorder should return the set of border points given a list of dig instructions`() {
        val border = digBorder(sampleInput)

        assert(border == parsePoints(sampleInputBorder))
    }

    @Test
    fun `digBorder should throw an exception when the border doesn't end where it begins`() {
        val badInstructions = sampleInput.dropLast(1)
        val exception: IllegalArgumentException = assertThrows { digBorder(badInstructions) }
        assert(exception.message == "Border must end at its start point")
    }

    @Test
    fun `digBorder should return a valid border given production input`() {
        val input = readInput("Day18")
        val border = digBorder(input)

        println("border size: ${border.size} points")
        val boundingBox = border.boundingBox()
        println("bounding box: $boundingBox with area ${boundingBox.area()}")
    }

    @Test
    fun `findInteriorPoints should return the set of interior points given the border points`() {
        val borderPoints = parsePoints(sampleInputBorder)
        val expectedInteriorPoints = parsePoints(sampleInputBorderAndInterior) - borderPoints

        val result = findInteriorPoints(borderPoints)

        assert(result == expectedInteriorPoints)
    }

    private fun parsePoints(input: List<String>): Set<Point> {
        return input
            .flatMapIndexed{ y, line -> line.mapIndexed { x, char -> Point(x,y) to char } }
            .filter { it.second == '#' }
            .map { it.first }
            .toSet()
    }

    private fun BoundingBox.area() : Int {
        val xSize = xBounds.last - xBounds.first
        val ySize = yBounds.last - yBounds.first
        return xSize * ySize
    }
}