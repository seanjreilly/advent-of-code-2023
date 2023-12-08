package day08

import utils.readInput
import java.math.BigInteger
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
    do {
        val currentNode = nodes[key]!!
        val index = (steps++ % directions.length).toInt()
        key = when (directions[index]) {
            'L' -> currentNode.left
            else -> currentNode.right
        }
    } while (key != "ZZZ")
    return steps
}

fun part2(input: List<String>): Long {
    val nodes = parse(input)
    val directions = input.first()

    // find all nodes that end with A, calculate the journey length for each node to a node that ends with Z
    // and then compute the Least Common Multiple of all journey lengths — this is
    // the number of steps when all the journey periods will synchronize
    return nodes.keys
        .filter { it.endsWith("A") }
        .map { countStepsUntilZNode(nodes, directions, it) }
        .map { it.toBigInteger() }
        .reduce(BigInteger::lcm).toLong()
}

private fun countStepsUntilZNode(nodes: Map<String, LeftRight>, directions: String, startPosition: String) : Long {
    var key = startPosition
    var steps = 0L
    do {
        val currentNode = nodes[key]!!
        val index = (steps++ % directions.length).toInt()
        key = when (directions[index]) {
            'L' -> currentNode.left
            else -> currentNode.right
        }
    } while (!key.endsWith("Z"))

    val stepsPart1 = steps
    val terminatorPart1 = key

    do {
        val currentNode = nodes[key]!!
        val index = (steps++ % directions.length).toInt()
        key = when (directions[index]) {
            'L' -> currentNode.left
            else -> currentNode.right
        }
    } while (!key.endsWith("Z"))

    val stepsPart2 = steps - stepsPart1
    assert(stepsPart2 == stepsPart1) { "This method only works when the repetition is the same length as the original number of steps" }
    assert(terminatorPart1 == key) {"This method only works when there's a simple cycle after the first __Z node is reached"}
//    println("$startPosition -- $stepsPart1 --> $terminatorPart1 -- $stepsPart2 --> $key")
    return stepsPart1
}

data class LeftRight(val left: String, val right: String)

internal fun parse(input: List<String>): Map<String, LeftRight> = input.drop(2).associate(::parseLine)

private fun parseLine(line: String): Pair<String, LeftRight> {
    val key = line.substringBefore('=').trim()
    val leftRight = line.substringAfter("(").substringBefore(")").split(", ").let { LeftRight(it[0], it[1]) }
    return Pair(key, leftRight)
}

internal fun BigInteger.lcm(other: BigInteger): BigInteger {
    val gcd: BigInteger = this.gcd(other)
    val absProduct: BigInteger = this.multiply(other).abs()
    return absProduct.divide(gcd)
}