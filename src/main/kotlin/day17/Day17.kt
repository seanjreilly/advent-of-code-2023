package day17

import utils.*
import utils.CardinalDirection.East
import java.util.*
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
    return findCostOfBestPathToFactory(buildGraph(parseEntryCosts(input))).toLong()
}

fun part2(input: List<String>): Long {
    return findCostOfBestPathToFactory(buildUltraCrucibleGraph(parseEntryCosts(input))).toLong()
}

typealias Graph = Map<PointAndDirection, Map<PointAndDirection, Int>>

internal fun parseEntryCosts(input: List<String>): Array<IntArray> =
    input.map { line -> line.toCharArray().map { it.digitToInt() }.toIntArray() }.toTypedArray()

internal operator fun Array<IntArray>.get(point: Point) = this[point.y][point.x]

internal fun buildGraph(entryCosts: Array<IntArray>): Graph {
    val validX = entryCosts.first().indices
    val validY = entryCosts.indices

    return validX.flatMap { x -> validY.map { y -> Point(x,y) } }
        .flatMap { point -> CardinalDirection.entries.map { direction -> point facing direction } }
        .associateBy( { it }) { (point, direction) ->
            val newDirections = listOf(direction.turn(TurnDirection.Left), direction.turn(TurnDirection.Right))

            // based on the rules, a legal sequence of moves is 1, 2, or 3 tiles in the given direction, followed by
            // either a left or a right turn. Model the entire sequence as a single graph edge.
            // The cost of the edge is the total entry costs for each square entered during the sequence.
            // Graph nodes are a point + a direction.

            var newPoint = point
            var costSoFar = 0
            val reachablePointsAndCosts = mutableListOf<Pair<Point, Int>>()
            for (i in 1..3) {
                newPoint = newPoint.move(direction)
                if ((newPoint.x !in validX) || (newPoint.y !in validY)) {
                    break // if this point isn't valid the next one(s) won't be either
                }
                costSoFar += entryCosts[newPoint]
                reachablePointsAndCosts += newPoint to costSoFar
            }

            val reachableNodesAndCosts = reachablePointsAndCosts
                .flatMap { (newPoint, cost) -> newDirections.map { newDirection -> (newPoint facing newDirection) to cost } } //cost isn't affected by the direction of the turn

            reachableNodesAndCosts.toMap()
        }
}

internal fun buildUltraCrucibleGraph(entryCosts: Array<IntArray>): Graph {
    val validX = entryCosts.first().indices
    val validY = entryCosts.indices

    return validX.flatMap { x -> validY.map { y -> Point(x,y) } }
        .flatMap { point -> CardinalDirection.entries.map { direction -> point facing direction } }
        .associateBy( { it }) { (point, direction) ->
            val newDirections = listOf(direction.turn(TurnDirection.Left), direction.turn(TurnDirection.Right))

            // based on the rules, a legal sequence of moves is 4-10 tiles in the given direction, followed by
            // either a left or a right turn. Model the entire sequence as a single graph edge.
            // The cost of the edge is the total entry costs for each square entered during the sequence.
            // Graph nodes are a point + a direction.

            var newPoint = point
            var costSoFar = 0
            val reachablePointsAndCosts = mutableListOf<Pair<Point, Int>>()
            for (i in 1..10) {
                newPoint = newPoint.move(direction)
                if ((newPoint.x !in validX) || (newPoint.y !in validY)) {
                    break // if this point isn't valid the next one(s) won't be either
                }
                costSoFar += entryCosts[newPoint]
                reachablePointsAndCosts += newPoint to costSoFar
            }

            val reachableNodesAndCosts = reachablePointsAndCosts
                .drop(3) //the first 3 squares contribute to heat costs, but aren't valid destinations
                .flatMap { (newPoint, cost) -> newDirections.map { newDirection -> (newPoint facing newDirection) to cost } } //cost isn't affected by the direction of the turn

            reachableNodesAndCosts.toMap()
        }
}

internal fun findCostOfBestPathToFactory(graph: Graph): Int {
    val startCondition = Point(0,0) facing East
    val tentativeDistances = graph.keys.associateWith { Int.MAX_VALUE }.toMutableMap()
    tentativeDistances[startCondition] = 0

    val unvisitedNodes = PriorityQueue<Pair<PointAndDirection, Int>>(compareBy { it.second })
    tentativeDistances.forEach { (pointAndDirection, distance) ->
        unvisitedNodes.add(Pair(pointAndDirection, distance))
    }

    val visitedNodes = mutableSetOf<PointAndDirection>()

    while (unvisitedNodes.isNotEmpty()) {
        val currentNode = unvisitedNodes.remove().first

        //do an extra filter to remove the duplicate entries from the priority queue (see below)
        if (currentNode in visitedNodes) {
            continue
        }

        visitedNodes += currentNode

        val distanceToCurrentNode = tentativeDistances[currentNode]!!
        if (distanceToCurrentNode == Int.MAX_VALUE) {
            break //we've reached an unreachable point
        }

        val neighbours = graph[currentNode]!!.entries
        neighbours
            .filter { it.key !in visitedNodes }
            .forEach { (newNode, transitionCost) ->
                val currentCostToNode = tentativeDistances[newNode]!!
                val altDistance = distanceToCurrentNode + transitionCost
                if (altDistance < currentCostToNode) { //filter out more expensive paths
                    tentativeDistances[newNode] = altDistance
                    unvisitedNodes.add(newNode to altDistance) //don't remove the old point (slow), just leave a duplicate entry
                }
            }
    }

    // because we're modelling the graph as a PointAndDirection, there are four possible end nodes: the end point facing in each direction
    // the cheapest cost out of these is the answer
    val endPoint = Point(graph.keys.maxOf { it.point.x }, graph.keys.maxOf { it.point.y })
    return CardinalDirection.entries
        .map { endPoint facing it }
        .minOf { tentativeDistances[it]!! }
}