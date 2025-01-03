package day01

import utils.LongSolution

fun main() = Day01Solution().run()
class Day01Solution : LongSolution() {
    override fun part1(input: List<String>) = input.map { firstAndLastDigits(it) }.sumOf { it.toLong() }
    override fun part2(input: List<String>) = input
        .map { firstDigitPart2(it).toString() + lastDigitPart2(it) }
        .sumOf { it.toLong() }

    internal fun firstAndLastDigits(line: String): String {
        return firstDigit(line).toString() + lastDigit(line)
    }

    internal fun firstDigit(line: String): Char {
        return line.first { it.isDigit() }
    }

    internal fun lastDigit(line: String): Char {
        return line.last { it.isDigit() }
    }

    internal val replacements = mapOf(
        "one" to '1',
        "two" to '2',
        "three" to '3',
        "four" to '4',
        "five" to '5',
        "six" to '6',
        "seven" to '7',
        "eight" to '8',
        "nine" to '9'
    )

    internal fun firstDigitPart2(line: String): Char {
        val first = line.first()
        if (first.isDigit()) {
            return first
        }
        replacements.forEach { (key, value) ->
            if (line.startsWith(key)) {
                return value
            }
        }
        return firstDigitPart2(line.substring(1))
    }

    internal fun lastDigitPart2(line: String): Char {
        val last = line.last()
        if (last.isDigit()) {
            return last
        }
        replacements.forEach { (key, value) ->
            if (line.endsWith(key)) {
                return value
            }
        }
        return lastDigitPart2(line.substring(0, line.length - 1))
    }
}
