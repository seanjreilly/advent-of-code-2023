package day21

import utils.*
import kotlin.math.sign

fun main() = Day21Solution().run()
class Day21Solution : LongSolution() {

    override fun part1(input: List<String>) = Garden(input).findLegalDestinations(64).size.toLong()

    override fun part2(input: List<String>) = Garden(input).countLegalDestinationsOnInfiniteGrid(26501365)
}

internal class Garden(val rocks: Set<Point>, val bounds: Bounds, val startPoint: Point) {

    internal val gardenSize = bounds.validXCoordinates.last + 1 //gardens are always square

    fun findLegalDestinations(steps: Int): Set<Point> {
        val distanceTree = djikstras(startPoint, neighboursMapping = ::neighboursMapping)
        return distanceTree
            // points that are reachable in fewer than N steps count IF the difference between N and the point's score
            // is a multiple of 2 (the elf can leave and re-enter a point in 2 steps)
            .filter { it.value == steps || (it.value < steps && (steps - it.value) % 2 == 0)}
            .map { it.key }
            .toSet()
    }

    fun countLegalDestinationsOnInfiniteGrid(steps: Int): Long {

        if (steps > 500) {
            // solve by finding the quadratic function
            // This only works for the production data, not the test data. Boo.
            val mod = steps % gardenSize
            val y0 = countLegalDestinationsOnInfiniteGrid(mod)
            val y1 = countLegalDestinationsOnInfiniteGrid(mod + gardenSize)
            val y2 = countLegalDestinationsOnInfiniteGrid(mod + (gardenSize * 2))

            /**
             * Use Lagrange's Interpolation formula for ax^2 + bx + c with x=[0,1,2] and y=[y0,y1,y2] we have
             *   f(x) = (x^2-3x+2) * y0/2 - (x^2-2x)*y1 + (x^2-x) * y2/2
             * so the coefficients are:
             * a = y0/2 - y1 + y2/2
             * b = -3*y0/2 + 2*y1 - y2/2
             * c = y0
             */

            //Checked this with wolfram alpha and it's the right answer

            val a = y0/2 - y1 + y2/2
            val b = -3*y0/2 + 2*y1 - y2/2
            val c = y0

            val x = steps.toLong() / gardenSize
            return (a * x * x) + (b * x) + c
        }


        val distanceTree = djikstras(startPoint, costLimit = steps, neighboursMapping = ::neighboursMappingInfiniteGrid)
        return distanceTree
            // points that are reachable in fewer than N steps count IF the difference between N and the point's score
            // is a multiple of 2 (the elf can leave and re-enter a point in 2 steps)
            .filter { it.value == steps || (it.value < steps && (steps - it.value) % 2 == 0)}
            .count()
            .toLong()
    }

    private fun neighboursMapping(currentPoint: Point)  = currentPoint
        .getCardinalNeighbours()
        .filter { it in bounds }
        .filter { it !in rocks }
        .map { it to 1 }

    fun neighboursMappingInfiniteGrid(currentPoint: Point) : Collection<Pair<Point, Int>> {
        return currentPoint
            .getCardinalNeighbours()
            .filter { translateToInBounds(it) !in rocks }
            .map { it to 1 }
    }

    private fun translateToInBounds(point: Point) : Point {
        if (point in bounds) {
            return point
        }

        val adjustedX = when(point.x.sign) {
            1 -> point.x % gardenSize
            0 -> point.x
            else -> gardenSize + (point.x % gardenSize) //mod of a negative is negative in kotlin
        }
        val adjustedY = when(point.y.sign) {
            1 -> point.y % gardenSize
            0 -> point.y
            else -> gardenSize + (point.y % gardenSize) //mod of a negative is negative in kotlin
        }
        return Point(adjustedX, adjustedY)
    }

    companion object {
        operator fun invoke(input: List<String>) : Garden {
            val (bounds, points) = parseGridWithPoints(input)
            val startPoint = points.first { it.second == 'S' }.first
            val rocks = points.filter { it.second == '#' }.map { it.first }.toSet()
            return Garden(rocks, bounds, startPoint)
        }
    }
}