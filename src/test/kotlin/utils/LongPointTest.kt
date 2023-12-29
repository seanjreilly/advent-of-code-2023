package utils

import org.junit.jupiter.api.Test

class LongPointTest {

    @Test
    fun `north should move 1 point north`() {
        val result = LongPoint(0, 0).north()
        assert(result == LongPoint(0, -1))
    }

    @Test
    fun `east should move 1 point east`() {
        val result = LongPoint(0, 0).east()
        assert(result == LongPoint(1, 0))
    }

    @Test
    fun `south should move 1 point south`() {
        val result = LongPoint(0, 0).south()
        assert(result == LongPoint(0, 1))
    }

    @Test
    fun `west should move 1 point west`() {
        val result = LongPoint(0, 0).west()
        assert(result == LongPoint(-1, 0))
    }

    @Test
    fun `manhattanDistance should return the manhattan distance between two points`() {
        val pointA = LongPoint(0,0)
        val pointB = LongPoint(3,5)

        val manhattanDistance = pointA.manhattanDistance(pointB)

        assert(manhattanDistance == 8L)
    }

    @Test
    fun `manhattanDistance should be reflexive`() {
        val pointA = LongPoint(0,0)
        val pointB = LongPoint(3,5)

        assert(pointA.manhattanDistance(pointB) == pointB.manhattanDistance(pointA))
    }

    @Test
    fun `LongBounds should only contain a point if it is within the bounds`() {
        val bounds = LongBounds(0 until 100, 0 until 100)
        assert(LongPoint(99,99) in bounds)
        assert(LongPoint(99,100) !in bounds)
    }
}