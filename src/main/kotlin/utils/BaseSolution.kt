package utils

import java.io.File
import java.net.URI
import java.nio.file.Files
import kotlin.system.measureTimeMillis

abstract class BaseSolution<T> {
    fun run() {
        print("Advent of Code year ${year}, day ${day}: ")
        val input = readInput()
        val elapsed = measureTimeMillis {
            println(part1(input))
            println(part2(input))
        }
        println()
        println("Elapsed time: $elapsed ms.")
    }

    private fun getAoCDay() : Int {
        val simpleName: String = this::class.java.packageName
        return DAY_REGEX.matchEntire(simpleName)!!.groupValues[1].toInt()
    }

    fun readInput() : List<String> {
        val dataFile = File("src/main/resources/Day${day}.txt")
        if (dataFile.exists()) {
            println("using cached download ${dataFile.path}")
        } else {
            val url = URI("https://adventofcode.com/${year}/day/${day}/input").toURL()
            println("downloading data file from ${url} to ${dataFile.path}")
            val credentials = File("src/main/resources/credentials.txt").readText().trim()
            url.openConnection().apply {
                setRequestProperty("Cookie", "session=${credentials}")
                setRequestProperty("Accept", "text/plain")
            }.getInputStream().use { Files.copy(it, dataFile.toPath()) }
        }
        return dataFile.readLines()
    }

    private val day by lazy { this.getAoCDay() }
    private val year = "2023"

    abstract fun part1(input: List<String>): T
    abstract fun part2(input: List<String>): T

    companion object {
        private val DAY_REGEX = """day(\d{2})""".toRegex(RegexOption.IGNORE_CASE)
    }
}

abstract class IntSolution : BaseSolution<Int>()
abstract class LongSolution : BaseSolution<Long>()