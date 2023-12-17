package day17

import org.junit.jupiter.api.Test
import utils.CardinalDirection.*
import utils.Point

class Day17Test {
    private val sampleInput = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent().lines()

    @Test
    fun `part1 should build a graph and return the heat cost of the best route to the factory`() {
        assert(part1(sampleInput) == 102L)
    }

    @Test
    fun `part2 should build an ultra crucible graph and return the heat cost of the best route to the factory`() {
        assert(part2(sampleInput) == 94L)
    }

    @Test
    fun `parseEntryCosts should return a 2d int array containing the entry cost for every point`() {
        val entryCosts: Array<IntArray> = parseEntryCosts(sampleInput)

        entryCosts.forEach { assert(it.size == 13) }
        assert(entryCosts.size == 13)
        assert(entryCosts[Point(0,0)] == 2)
        assert(entryCosts[Point(0,12)] == 4)
        assert(entryCosts[Point(12,0)] == 3)

        assert(entryCosts[Point(1,1)] == 2)
        assert(entryCosts[Point(2,2)] == 5)
        assert(entryCosts[Point(3,3)] == 6)
        assert(entryCosts[Point(4,4)] == 6)
        assert(entryCosts[Point(5,5)] == 9)
    }
    
    @Test
    fun `buildGraph should return a weighted graph where PointAndDirection nodes can reach other nodes within up to 3 steps and a turn`() {
        val graph: Graph = buildGraph(parseEntryCosts(sampleInput))

        assert(graph.size == 13 * 13 * 4) //a node for each square, pointing in each direction

        val startPoint = Point(0, 0)
        val entryCondition = startPoint facing East
        val nodesReachableFromEntry = graph[entryCondition]!! //test the nodes reachable from the start condition
        assert(nodesReachableFromEntry.size == 6)

        //can travel straight from 1-3 nodes, and then turn left or right
        //expected weights are the cumulative costs of entering points from the beginning of the turn
        assert(nodesReachableFromEntry[startPoint.move(East) facing North] == 4)
        assert(nodesReachableFromEntry[startPoint.move(East) facing South] == 4)
        assert(nodesReachableFromEntry[startPoint.move(East).move(East) facing North] == 5)
        assert(nodesReachableFromEntry[startPoint.move(East).move(East) facing South] == 5)
        assert(nodesReachableFromEntry[startPoint.move(East).move(East).move(East) facing North] == 8)
        assert(nodesReachableFromEntry[startPoint.move(East).move(East).move(East) facing South] == 8)
    }

    @Test
    fun `buildGraph should not include any edges that would lead out of bounds`() {
        val graph: Graph = buildGraph(parseEntryCosts(sampleInput))

        val node = Point(0,0) facing North
        val otherNodesReachableFromNode = graph[node]!!
        assert(otherNodesReachableFromNode.isEmpty())
    }

    @Test
    fun `buildUltraCrucibleGraph should return a weighted graph where PointAndDirection nodes can reach other nodes within 4 to 10 steps and a turn`() {
        val graph: Graph = buildUltraCrucibleGraph(parseEntryCosts(sampleInput))

        assert(graph.size == 13 * 13 * 4) //a node for each square, pointing in each direction

        val startPoint = Point(0, 0)
        val entryCondition = startPoint facing East
        val nodesReachableFromEntry = graph[entryCondition]!! //test the nodes reachable from the start condition
        assert(nodesReachableFromEntry.size == 14)

        //can travel straight from 4-10 nodes, and then turn left or right
        //expected weights are the cumulative costs of entering points from the beginning of the turn (including the first 3)
        //8 before we start
        assert(nodesReachableFromEntry[startPoint.copy(x=4) facing North] == 12)
        assert(nodesReachableFromEntry[startPoint.copy(x=4) facing South] == 12)
        assert(nodesReachableFromEntry[startPoint.copy(x=5) facing North] == 15)
        assert(nodesReachableFromEntry[startPoint.copy(x=5) facing South] == 15)
        assert(nodesReachableFromEntry[startPoint.copy(x=6) facing North] == 17)
        assert(nodesReachableFromEntry[startPoint.copy(x=6) facing South] == 17)
        assert(nodesReachableFromEntry[startPoint.copy(x=7) facing North] == 20)
        assert(nodesReachableFromEntry[startPoint.copy(x=7) facing South] == 20)
        assert(nodesReachableFromEntry[startPoint.copy(x=8) facing North] == 21)
        assert(nodesReachableFromEntry[startPoint.copy(x=8) facing South] == 21)
        assert(nodesReachableFromEntry[startPoint.copy(x=9) facing North] == 22)
        assert(nodesReachableFromEntry[startPoint.copy(x=9) facing South] == 22)
        assert(nodesReachableFromEntry[startPoint.copy(x=10) facing North] == 25)
        assert(nodesReachableFromEntry[startPoint.copy(x=10) facing South] == 25)
    }

    @Test
    fun `buildUltraCrucibleGraph should not include any edges that would lead out of bounds`() {
        val graph: Graph = buildUltraCrucibleGraph(parseEntryCosts(sampleInput))

        val node = Point(0,0) facing North
        val otherNodesReachableFromNode = graph[node]!!
        assert(otherNodesReachableFromNode.isEmpty())
    }

    @Test
    fun `buildUltraCrucibleGraph should has to enforce a minimum of 4 steps and if that would be out of bounds there should be no edges from the node`() {
        val graph: Graph = buildUltraCrucibleGraph(parseEntryCosts(sampleInput))

        val node = Point(9,12) facing East
        val otherNodesReachableFromNode = graph[node]!!
        assert(otherNodesReachableFromNode.isEmpty())
    }

    @Test
    fun `findCostOfBestPathToFactory should return the heat cost of the best possible path to the factory from the start`() {
        val graph: Graph = buildGraph(parseEntryCosts(sampleInput))
        val cost: Int = findCostOfBestPathToFactory(graph)
        assert(cost == 102)
    }

    @Test
    fun `findCostOfBestPathToFactory should return the correct value with an alternate graph and ultra crucible rules`() {
        val alternateInput = """
            111111111111
            999999999991
            999999999991
            999999999991
            999999999991
        """.trimIndent().lines()

        val graph = buildUltraCrucibleGraph(parseEntryCosts(alternateInput))
        val cost = findCostOfBestPathToFactory(graph)
        assert (cost == 71)
    }
}