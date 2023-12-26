package day25

import org.jgrapht.Graph
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day25")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return cut(parse(input)).map { it.toLong() }.reduce(Long::times)
}

fun part2(input: List<String>): Long {
    return 0
}

internal fun parse(input: List<String>): Graph<String, DefaultEdge> {
    val graph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    input
        .map { line -> line.substringBefore(":") to line.substringAfter(": ").split(" ")}
        .flatMap { it.second.map { v -> it.first to v } }
        .filter { it.second.isNotBlank() }
        .forEach { (source, destination) ->
            graph.addVertex(source)
            graph.addVertex(destination)
            graph.addEdge(source, destination)
        }
    return graph
}

internal fun cut(graph: Graph<String, DefaultEdge>): Set<Int> {
    val cutAlgorithm = StoerWagnerMinimumCut(graph)
    val minCut = cutAlgorithm.minCut()
    val componentGraphs = setOf(minCut, (graph.vertexSet() - minCut))
    return componentGraphs.map { it.size }.toSet()
}