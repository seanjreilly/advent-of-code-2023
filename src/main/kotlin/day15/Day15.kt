package day15

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day15")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return parseInitializationSequence(input)
        .sumOf { it.hash().toLong() }
}

fun part2(input: List<String>): Long {
    val boxes = processInitializationSequence(parseInitializationSequence(input))

    return boxes.flatMapIndexed { boxIndex, box ->
            box.mapIndexed { lensIndex, labelAndFocalLength -> Triple(boxIndex, lensIndex, labelAndFocalLength) }
        }
        .map {(boxIndex, lensIndex, labelAndFocalLength) -> (boxIndex + 1) * (lensIndex + 1) * labelAndFocalLength.focalLength}
        .sumOf { it.toLong() }
}

internal fun String.hash(): Int {
    var result = 0
    for (c: Char in this.toCharArray()) {
        result += c.code
        result *= 17
        result %= 256
    }
    return result
}

private fun parseInitializationSequence(input: List<String>) = input.first().split(',').filter { it.isNotBlank() }

internal fun processInitializationSequence(instructions: List<String>): List<List<LabelAndFocalLength>> {
    val boxes = List(256) { mutableListOf<LabelAndFocalLength>() }
    instructions.forEach { instruction ->
        when {
            instruction.endsWith('-') -> {
                val label = instruction.substringBefore('-')
                val box = boxes[label.hash()]
                box.removeIf { it.label == label }
            }
            else -> {
                val label = instruction.substringBefore('=')
                val focalLength = instruction.substringAfter('=').toInt()
                val box = boxes[label.hash()]
                when (val index = box.indexOfFirst { it.label == label }) {
                    -1 -> box.add(LabelAndFocalLength(label, focalLength))
                    else -> box[index].focalLength = focalLength
                }
            }
        }
    }
    return boxes
}

internal data class LabelAndFocalLength(val label: String, var focalLength: Int)