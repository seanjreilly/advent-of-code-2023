package day17

import org.junit.jupiter.api.Test
import utils.CardinalDirection.*
import utils.Point

class SolutionTest {
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

    private val solution = Solution()

    @Test
    fun `part1 should build a graph and return the heat cost of the best route to the factory`() {
        assert(solution.part1(sampleInput) == 102)
    }

    @Test
    fun `part2 should build an ultra crucible graph and return the heat cost of the best route to the factory`() {
        assert(solution.part2(sampleInput) == 94)
    }

    @Test
    fun `parseEntryCosts should return a 2d int array containing the entry cost for every point`() {
        val entryCosts: Array<IntArray> = parseEntryCosts(sampleInput)

        entryCosts.forEach { assert(it.size == 13) }
        assert(entryCosts.size == 13)
        assert(entryCosts[Point(0, 0)] == 2)
        assert(entryCosts[Point(0, 12)] == 4)
        assert(entryCosts[Point(12, 0)] == 3)

        assert(entryCosts[Point(1, 1)] == 2)
        assert(entryCosts[Point(2, 2)] == 5)
        assert(entryCosts[Point(3, 3)] == 6)
        assert(entryCosts[Point(4, 4)] == 6)
        assert(entryCosts[Point(5, 5)] == 9)
    }

    @Test
    fun `findNeighbours should return other nodes within 1 to 3 steps and a turn as neighbours for part 1`() {
        val entryCosts = parseEntryCosts(sampleInput)
        val startPoint = Point(0, 0)
        val entryCondition = startPoint facing East

        val neighbours = findNeighbours(entryCondition, entryCosts, 1..3) //test the nodes reachable from the start condition

        assert(neighbours.size == 6)

        //can travel straight from 1-3 nodes, and then turn left or right
        //expected weights are the cumulative costs of entering points from the beginning of the turn
        assert((startPoint.move(East) facing North to 4) in neighbours)
        assert((startPoint.move(East) facing South to 4) in neighbours)
        assert((startPoint.move(East).move(East) facing North to 5) in neighbours)
        assert((startPoint.move(East).move(East) facing South to 5) in neighbours)
        assert((startPoint.move(East).move(East).move(East) facing North to 8) in neighbours)
        assert((startPoint.move(East).move(East).move(East) facing South to 8) in neighbours)
    }

    @Test
    fun `findNeighbours should not include any nodes that are out of bounds`() {
        val entryCosts = parseEntryCosts(sampleInput)
        val node = Point(0, 0) facing North
        assert(findNeighbours(node, entryCosts, 1..3).isEmpty())
    }

    @Test
    fun `findNeighbours should return nodes within 4 to 10 steps and a turn for part 2`() {
        val entryCosts = parseEntryCosts(sampleInput)
        val startPoint = Point(0, 0)
        val entryCondition = startPoint facing East

        val neighbours = findNeighbours(entryCondition, entryCosts, 4..10) //test the nodes reachable from the start condition
        assert(neighbours.size == 14)

        //can travel straight from 4-10 nodes, and then turn left or right
        //expected weights are the cumulative costs of entering points from the beginning of the turn (including the first 3)
        //8 before we start
        assert(((startPoint.copy(x = 4) facing North) to 12) in neighbours)
        assert(((startPoint.copy(x = 4) facing South) to 12) in neighbours)
        assert(((startPoint.copy(x = 5) facing North) to 15) in neighbours)
        assert(((startPoint.copy(x = 5) facing South) to 15) in neighbours)
        assert(((startPoint.copy(x = 6) facing North) to 17) in neighbours)
        assert(((startPoint.copy(x = 6) facing South) to 17) in neighbours)
        assert(((startPoint.copy(x = 7) facing North) to 20) in neighbours)
        assert(((startPoint.copy(x = 7) facing South) to 20) in neighbours)
        assert(((startPoint.copy(x = 8) facing North) to 21) in neighbours)
        assert(((startPoint.copy(x = 8) facing South) to 21) in neighbours)
        assert(((startPoint.copy(x = 9) facing North) to 22) in neighbours)
        assert(((startPoint.copy(x = 9) facing South) to 22) in neighbours)
        assert(((startPoint.copy(x = 10) facing North) to 25) in neighbours)
        assert(((startPoint.copy(x = 10) facing South) to 25) in neighbours)
    }

    @Test
    fun `findNeighbours should not include any nodes that are out of bounds in part 2`() {
        val entryCosts = parseEntryCosts(sampleInput)
        val node = Point(9, 12) facing East
        assert(findNeighbours(node, entryCosts, 4..10).isEmpty())
    }

    @Test
    fun `findCostOfBestPathToFactory should return the heat cost of the best possible path to the factory from the start`() {
        val entryCosts = parseEntryCosts(sampleInput)
        val cost: Int = findCostOfBestPathToFactory(entryCosts, 1..3)
        assert(cost == 102)
    }

    @Test
    fun `findCostOfBestPathToFactory should return the heat cost of the best possible path to the factory with part2 rules`() {
        val entryCosts = parseEntryCosts(sampleInput)
        val cost: Int = findCostOfBestPathToFactory(entryCosts, 4..10)
        assert(cost == 94)
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

        val entryCosts = parseEntryCosts(alternateInput)
        val cost = findCostOfBestPathToFactory(entryCosts, 4..10)
        assert(cost == 71)
    }

    @Test
    fun `findCostOfBestPathToFactory should allow the crucible to start facing south`() {
        val alternateInput = """
            1
            2
            3
            4
        """.trimIndent().lines()

        val entryCosts = parseEntryCosts(alternateInput)
        val cost = findCostOfBestPathToFactory(entryCosts, 1..3)
        assert(cost == 9)
    }

    @Test
    fun `findCostOfBestPathToFactory should allow the crucible to start facing east`() {
        val alternateInput = """
            1234
        """.trimIndent().lines()

        val entryCosts = parseEntryCosts(alternateInput)
        val cost = findCostOfBestPathToFactory(entryCosts, 1..3)
        assert(cost == 9)
    }
}