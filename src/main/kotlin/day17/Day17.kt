package day17

import utils.*
import utils.CardinalDirection.East
import utils.CardinalDirection.South
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day17")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return findCostOfBestPathToFactory(parseEntryCosts(input), 1..3).toLong()
}

fun part2(input: List<String>): Long {
    return findCostOfBestPathToFactory(parseEntryCosts(input), 4..10).toLong()
}

internal fun findCostOfBestPathToFactory(entryCosts: Array<IntArray>, legalMovesBeforeTurning: IntRange) : Int {
    val validX = entryCosts.first().indices
    val validY = entryCosts.indices

    val origin = Point(0,0)
    val startNodes = arrayOf(origin facing East, origin facing South)
    val distances = djikstras(*startNodes) { findNeighbours(it, entryCosts, legalMovesBeforeTurning) }

    // because we're modelling the graph as a PointAndDirection, there are four possible end nodes: the end point facing in each direction
    // the cheapest cost out of these is the answer
    val endPoint = Point(validX.last, validY.last)
    return CardinalDirection.entries
        .map { endPoint facing it }
        .map { distances[it] }
        .filterNotNull()
        .min()
}

internal fun findNeighbours(node: PointAndDirection, entryCosts: Array<IntArray>, legalMovesBeforeTurning: IntRange) : Collection<Pair<PointAndDirection, Int>> {
    val validX = entryCosts.first().indices
    val validY = entryCosts.indices
    val newDirections = listOf(node.direction.turn(TurnDirection.Left), node.direction.turn(TurnDirection.Right))

    // based on the rules, a legal sequence of moves is N-M tiles in the given direction, followed by
    // either a left or a right turn. Model the entire sequence as a single graph edge.
    // The cost of the edge is the total entry costs for each square entered during the sequence.
    // Graph nodes are a point + a direction.
    var newPoint = node.point
    var costSoFar = 0
    val reachablePointsAndCosts = mutableListOf<Pair<Point, Int>>()
    for (i in 1..legalMovesBeforeTurning.last) {
        newPoint = newPoint.move(node.direction)
        if ((newPoint.x !in validX) || (newPoint.y !in validY)) {
            break // if this point isn't valid the next one(s) won't be either
        }
        costSoFar += entryCosts[newPoint]
        reachablePointsAndCosts += newPoint to costSoFar
    }

    return reachablePointsAndCosts
        .filterIndexed { index, _ -> index + 1 in legalMovesBeforeTurning }
        .flatMap { (newPoint, cost) -> newDirections.map { newDirection -> (newPoint facing newDirection) to cost } } //cost isn't affected by the direction of the turn
}

internal fun parseEntryCosts(input: List<String>): Array<IntArray> =
    input.map { line -> line.toCharArray().map { it.digitToInt() }.toIntArray() }.toTypedArray()

internal operator fun Array<IntArray>.get(point: Point) = this[point.y][point.x]