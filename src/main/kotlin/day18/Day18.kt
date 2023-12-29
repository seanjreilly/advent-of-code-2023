package day18

import utils.LongPoint
import utils.readInput
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day18")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return countInteriorAndBorderPoints(parsePolygon(input))
}

fun part2(input: List<String>): Long {
    return countInteriorAndBorderPoints(parsePolygon(convertHexCodeToDigInstructions(input)))
}

internal fun parsePolygon(instructions: List<String>): Polygon {
    var lastPoint = LongPoint(0, 0) //starts here
    val polygon = mutableListOf(lastPoint)

    instructions
        .map { it.substringBefore(' ') to it.substringAfter(' ').substringBefore(' ').toInt() }
        .forEach { (direction, squares) ->
            lastPoint = when (direction) {
                "U" -> LongPoint(lastPoint.x, lastPoint.y - squares)
                "D" -> LongPoint(lastPoint.x, lastPoint.y + squares)
                "L" -> LongPoint(lastPoint.x - squares, lastPoint.y)
                "R" -> LongPoint(lastPoint.x + squares, lastPoint.y)
                else -> throw IllegalArgumentException("Unexpected direction '$direction'")
            }
            polygon += lastPoint
        }

    require(lastPoint == LongPoint(0,0)) {"Polygon must end at its start point"}
    return polygon
}

internal fun countInteriorAndBorderPoints(polygon: Polygon): Long {
    //use the shoelace theorem to calculate the area
    val shoelaces = polygon
        .windowed(2, 1)
        .map { (p1, p2) ->
            val (x1, y1) = p1
            val (x2, y2) = p2
            (x1*y2) to (y1 * x2)
        }
    val area = abs(shoelaces.sumOf { it.first } - shoelaces.sumOf { it.second }) / 2L

    //calculate the perimeter length aka the number of exterior points
    val perimeterLength = polygon.windowed(2, 1).sumOf { (p1, p2) -> p1.manhattanDistance(p2) }

    //use pick's theorem to solve for interior points (i = A + 1 - b/2)
    val interiorPoints = area + 1 - (perimeterLength / 2L)

    //return interior points + exterior points
    return interiorPoints + perimeterLength
}

internal fun convertHexCodeToDigInstructions(input: List<String>): List<String> {
    return input
        .map {line -> line.substringAfter('#').substringBefore(')')  }
        .map { hexCode -> hexCode.take(5).toInt(16) to hexCode.takeLast(1) }
        .map { (distance, encodedDirection) ->
            val direction = when(encodedDirection) {
                "0" -> "R"
                "1" -> "D"
                "2" -> "L"
                "3" -> "U"
                else -> throw IllegalArgumentException("Unexpected encoded direction '$encodedDirection'")
            }
            "$direction $distance"
        }
}

typealias Polygon = List<LongPoint>