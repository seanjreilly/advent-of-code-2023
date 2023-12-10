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
    return findLoopLength(input).toLong() / 2
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun findLoopLength(input: List<String>): Int {
    val s = findS(input)
    val startPoints = findLegalStartingPoints(input, s).toList()

    var lastPoint = s
    var currentPoint = startPoints.first() //arbitrary starting direction
    var stepsSoFar = 1

    do {
        stepsSoFar++
        val nextPoint = input[currentPoint]
            .nextDirections()
            .toList()
            .map { it(currentPoint) }
            .first { it != lastPoint } //keep going in the same direction
        lastPoint = currentPoint
        currentPoint = nextPoint
    } while (currentPoint != s)
    return stepsSoFar
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
