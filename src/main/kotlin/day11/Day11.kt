package day11

import utils.Point
import utils.readInput
import java.util.*
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day11")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: Image): Long {
    val expandedGalaxies = expandUniverse(findGalaxies(input), input)
    return findTotalDistanceBetweenGalaxies(expandedGalaxies)
}

fun part2(input: List<String>): Long {
    val expandedGalaxies = expandUniverse(findGalaxies(input), input, 1_000_000)
    return findTotalDistanceBetweenGalaxies(expandedGalaxies)
}

typealias Image = List<String>
typealias Galaxy = Point

internal fun findGalaxies(image: Image): Set<Galaxy> {
    return image
        .flatMapIndexed {y, line -> line.mapIndexed { x, char -> Triple(x, y, char) } }
        .filter { it.third == '#' }
        .map { Galaxy(it.first, it.second) }
        .toSet()
}

internal fun expandUniverse(galaxies: Set<Galaxy>, image: Image, expansionFactor:Int = 2): Set<Galaxy> {
    val uniqueYValuesInGalaxyCoordinates = galaxies.map { it.y }.toSet()
    val emptyRows = TreeSet(image.indices.toSet() - uniqueYValuesInGalaxyCoordinates)

    val uniqueXValuesInGalaxyCoordinates = galaxies.map { it.x }.toSet()
    val emptyColumns = TreeSet((0 until image.first().length).toSet() - uniqueXValuesInGalaxyCoordinates)

    return galaxies.map {(oldX, oldY) ->
        val newX = oldX + (emptyColumns.headSet(oldX).size * (expansionFactor - 1))
        val newY = oldY + (emptyRows.headSet(oldY).size * (expansionFactor - 1))
        Galaxy(newX, newY)
    }.toSet()
}

internal fun findTotalDistanceBetweenGalaxies(expandedGalaxies: Set<Galaxy>): Long {
    // we want unique pairs, and this method calculates the distance for every pair twice,
    // once from A->B and once from B->A
    // divide the answer by 2 to adjust
    return expandedGalaxies
        .map { galaxy ->
            galaxy to expandedGalaxies.filter { it != galaxy }
        }
        .flatMap { (galaxy, otherGalaxies) -> otherGalaxies.map { og -> galaxy.manhattanDistance(og) } }
        .sumOf { it.toLong() } / 2
}