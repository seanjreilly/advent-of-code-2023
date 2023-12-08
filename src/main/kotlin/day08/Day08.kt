package day08

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day08")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val directions = input.first()
    val nodes = parse(input)

    var steps = 0L
    var key = "AAA"
    while (key != "ZZZ") {
        val currentNode = nodes[key]!!
        val index = (steps++ % directions.length).toInt()
        key = when (directions[index]) {
            'L' -> currentNode.left
            else -> currentNode.right
        }
    }
    return steps
}

fun part2(input: List<String>): Long {
    return 0
}

data class LeftRight(val left: String, val right: String)

internal fun parse(input: List<String>): Map<String, LeftRight> = input.drop(2).associate(::parseLine)

private fun parseLine(line: String): Pair<String, LeftRight> {
    val key = line.substringBefore('=').trim()
    val leftRight = line.substringAfter("(").substringBefore(")").split(", ").let { LeftRight(it[0], it[1]) }
    return Pair(key, leftRight)
}