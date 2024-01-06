package day23

import utils.Bounds
import utils.Point
import utils.parseGridWithPoints
import utils.readInput
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day23")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return findLongestPath(input).toLong()
}

fun part2(input: List<String>): Long {
    return findLongestPathPart2(input).toLong()
}

internal fun findLongestPath(input: List<String>): Int {
    val predicate: (Point, Point, Char) -> Boolean = { newPoint, currentPoint, tileValue ->
        when (tileValue) {
            '.' -> true
            '^' -> newPoint == currentPoint.north()
            'v' -> newPoint == currentPoint.south()
            '>' -> newPoint == currentPoint.east()
            '<' -> newPoint == currentPoint.west()
            else -> false//really #
        }
    }

    return findPath(input, predicate)
}

internal fun findLongestPathPart2(input: List<String>): Int {
    return findPath(input) { _, _, tileValue -> tileValue != '#' }
}

private fun findPath(input: List<String>, predicate: (Point, Point, Char) -> Boolean): Int {
    require(input.first().count { it == '.' } == 1)
    require(input.last().count { it == '.' } == 1)
    val (bounds, rawData) = parseGridWithPoints(input)
    val data = rawData.toMap()

    val start = data.entries.first { it.key.y == 0 && it.value == '.' }.key
    val end = data.entries.first { it.key.y == bounds.lastY && it.value == '.' }.key

    val graph = buildOptimisedGraph(data, bounds, start, end, predicate)

    val queue = ArrayDeque<Triple<Point, Int, Set<Point>>>()
    var maxCost = Int.MIN_VALUE
    queue += Triple(start, 0, emptySet())
    while (queue.isNotEmpty()) {
        val (point, cost, visitedPoints) = queue.removeFirst()
        if (point == end) {
            maxCost = max(maxCost, cost)
            continue
        }

        val updatedVisitedPoints = visitedPoints + point
        graph[point]!!
            .filter { it.destination !in updatedVisitedPoints }
            .forEach { edge -> queue += Triple(edge.destination, cost + edge.cost, updatedVisitedPoints) }
    }
    return maxCost
}

/**
 * Optimise the graph into a weighted graph of junction nodes
 * The weight for each edge is the number of tiles that would
 * be visited in the original graph going to the destination
 * node from the source node
 */
private fun buildOptimisedGraph(
    data: Map<Point, Char>, bounds: Bounds,
    start: Point, end: Point,
    predicate: (Point, Point, Char) -> Boolean
): Map<Point, Set<OptimisedEdge>> {

    fun findJunctionNeighbours(point: Point): Collection<Point> {
        return point.getCardinalNeighbours()
            .filter { it in bounds }
            .filter { data[it]!! != '#' } //need to use a relaxed criteria here because the neighbour count is used in both directions
    }

    // find junction nodes: anywhere we can make a meaningful choice (plus start and end)
    val optimisedNodesAndNeighbours = data.keys
        .map { it to findJunctionNeighbours(it) }
        .filter { it.first in listOf(start, end) || it.second.size > 2 } //find junctions, start and end

    val optimisedNodes = optimisedNodesAndNeighbours.map { it.first }.toSet()

    // for each path leaving a junction node, find the next junction node it gets to and the number of steps (edge weight)
    fun findNeighbours(point: Point): Collection<Point> {
        return point.getCardinalNeighbours()
            .filter { it in bounds }
            .filter { predicate(it, point, data[it]!!) }
    }

    fun buildEdge(currentPoint: Point, lastPoint: Point, cost: Int): OptimisedEdge? {
        if (currentPoint in optimisedNodes) {
            return OptimisedEdge(currentPoint, cost)
        }

        // max of 2 neighbours by definition
        // 1 neighbour means we've hit a dead end
        val nextPoint = findNeighbours(currentPoint).firstOrNull() { it != lastPoint }
        return nextPoint?.let { buildEdge(it, currentPoint, cost + 1) } //return null if this is a dead end
    }

    val graph = optimisedNodesAndNeighbours.associate { (point, neighbours) ->
        point to neighbours
            .mapNotNull { neighbour -> buildEdge(neighbour, point, 1) }
            .toSet()
    }

    println("\t graph optimised to ${graph.size} nodes")
    return graph
}

private class OptimisedEdge(val destination: Point, val cost: Int)