package day10

import utils.Point
import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day10")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return findLoop(input).size.toLong() / 2
}

fun part2(input: List<String>): Long {
    val loop = findLoop(input)
    val upFacingSegments = "L|J"
    var tilesInsideTheLoopSoFar = 0L

    input.mapIndexed { y, line ->
        // use a ray-casting algorithm to find tiles that are "inside the loop" on each line
        // an odd number of intersections from the beginning of the line counts as "inside"
        var intersectionsSoFarThisLine = 0
        for (p: Point in line.mapIndexed { x, _ -> Point(x, y) }) {
            if (p in loop) {
                var tile = input[p]
                if (tile == 'S') { tile = replaceSWithOriginalCharacter(input, p) }
                if (tile in upFacingSegments) {
                    intersectionsSoFarThisLine++ //only segments that "cross" the horizontal count as intersections
                }
            } else if (intersectionsSoFarThisLine % 2 == 1) {
                tilesInsideTheLoopSoFar++
            }
        }
    }
    return tilesInsideTheLoopSoFar
}

internal fun findLoop(input: List<String>): Set<Point> {
    val s = findS(input)
    val startPoints = findLegalStartingPoints(input, s).toList()

    val path = mutableListOf(s)
    var currentPoint = startPoints.first() //arbitrary starting direction

    do {
        val nextPoint = input[currentPoint]
            .nextDirections()
            .toList()
            .map { it(currentPoint) }
            .first { it != path.last() } //keep going in the same direction
        path += currentPoint
        currentPoint = nextPoint
    } while (currentPoint != s)
    return path.toSet()
}

internal fun Char.nextDirections(): Pair<(Point) -> Point, (Point) -> Point> = when (this) {
    '|' -> Point::north to Point::south
    '-' -> Point::east to Point::west
    'L' -> Point::north to Point::east
    'J' -> Point::north to Point::west
    '7' -> Point::south to Point::west
    'F' -> Point::south to Point::east
    else -> throw IllegalArgumentException("'$this' is not a legal pipe character")
}

internal fun findS(input: List<String>): Point = input
    .mapIndexed { index, line -> Point(line.indexOf('S'), index) }
    .first { it.x != -1 }

internal fun Point.isWithinBounds(input: List<String>) = x in 0 until input.first().length && y in input.indices

private fun replaceSWithOriginalCharacter(input: List<String>, s: Point): Char {
    val pipeStartPoints = findLegalStartingPoints(input, s).toList().toSet()
    return "|-LJ7F"
        .toCharArray()
        .map { it to it.nextDirections().toList().map { nd -> nd(s) }.toSet() }
        .filter { it.second == pipeStartPoints }
        .map { it.first }
        .first()
}

private fun findLegalStartingPoints(input: List<String>, s: Point): Pair<Point, Point> {
    val startPoints = s.getCardinalNeighbours()
        .asSequence()
        .filter { it.isWithinBounds(input) }
        .filter { input[it] != '.' }
        .map { Pair(it, input[it].nextDirections()) }
        .filter { s in it.second.toList().map { fn -> fn(it.first) } }
        .map { it.first }
        .toList()

    assert(startPoints.size == 2) { "the pipe can only have 2 start points" }
    return Pair(startPoints.first(), startPoints.last())
}

private operator fun List<String>.get(p: Point) : Char {
    return this[p.y][p.x]
}