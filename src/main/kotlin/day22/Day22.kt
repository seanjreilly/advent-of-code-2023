package day22

import utils.readInput
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day22")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return input.map { parseBrick(it) }
        .moveAllDown()
        .findSafeBricksToDisintegrate()
        .size
        .toLong()
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun parseBrick(line: String): Brick {
    val (first, last) = line.split('~').map { Point3D(it) }
    return Brick(first, last)
}

internal data class Brick(val firstPoint: Point3D, val lastPoint: Point3D) : Iterable<Point3D> {
    val minZ  = min(firstPoint.z, lastPoint.z)
    val aboveGround = minZ > 1
    val bottomPoints by lazy { this.filter { it.z == minZ }.toSet() }
    override fun iterator(): Iterator<Point3D> = iterator {
        for (x in firstPoint.x..lastPoint.x) {
            for (y in firstPoint.y..lastPoint.y) {
                for (z in firstPoint.z..lastPoint.z) {
                    yield(Point3D(x, y, z))
                }
            }
        }
    }

    fun moveDown(): Brick {
        check(aboveGround) { "brick is on the ground and cannot move down" }
        return Brick(firstPoint.down(), lastPoint.down())
    }
}

internal data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun down(): Point3D = this.copy(z = z - 1)

    companion object {
        operator fun invoke(str: String) : Point3D {
            val (x,y,z) = str.split(',').map { it.toInt() }
            return Point3D(x,y,z)
        }
    }
}

internal fun Collection<Brick>.moveAllDown(): Collection<Brick> {
    val reverseIndex = buildReverseIndex().toMutableMap()
    val sortedBricks = this.sortedByDescending { it.minZ }.toMutableList()
    val result = mutableListOf<Brick>()

    nextBrick@while (sortedBricks.isNotEmpty()) {
        var brick = sortedBricks.removeLast()
        reverseIndex -= brick
        thisBrick@while (brick.aboveGround) {
            /*
                We can use a simple algorithm here, because
                bricks are never narrow at the bottom and wider at higher levels
             */
            val brickMovedDown = brick.moveDown()
            val spaceBelowThatHasToBeFree = brickMovedDown.bottomPoints
            if (spaceBelowThatHasToBeFree.any { it in reverseIndex.keys }) {
                break@thisBrick
            }
            brick = brickMovedDown
        }

        reverseIndex += brick.map { it to brick }
        result += brick
    }
    return result
}

internal fun Collection<Brick>.findSafeBricksToDisintegrate(): Set<Brick> {
    val reverseIndex = buildReverseIndex()
    val criticalBricks = mutableSetOf<Brick>()

    this.filter { it.aboveGround } //bricks on the ground can't be critical bricks
        .forEach { brick ->
            val supportingBricks = brick
                .moveDown()
                .bottomPoints
                .mapNotNull { reverseIndex[it] } //find bricks that support this brick using the reverse index
                .toSet()

            //if there's only one brick supporting this brick, that brick is critical
            if (supportingBricks.size == 1) {
                criticalBricks += supportingBricks
            }
        }

    return (this - criticalBricks).toSet()
}

private fun Collection<Brick>.buildReverseIndex() = flatMap { brick -> brick.map { point -> point to brick } }.toMap()
