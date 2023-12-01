package utils

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class PointTest {
    @Test
    fun `manhattanDistance should return the manhattan distance between two points`() {
        val pointA = Point(0,0)
        val pointB = Point(3,5)

        val manhattanDistance = pointA.manhattanDistance(pointB)

        assert(manhattanDistance == 8)
    }

    @Test
    fun `manhattanDistance should be reflexive`() {
        val pointA = Point(0,0)
        val pointB = Point(3,5)

        assert(pointA.manhattanDistance(pointB) == pointB.manhattanDistance(pointA))
    }

    @Test
    fun `pointsWithManhattanDistance should return all Points with a given manhattan distance from this point`() {
        val point = Point(0,0)
        val manhattanDistance = 3

        val expectedResult = setOf(
            //this is a set, so just duplicate some squares for obviousness and it will all work out
            Point(0, 3),
            Point(1, 2),
            Point(2,1),
            Point(3,0),
            Point(0, -3),
            Point(1, -2),
            Point(2, -1),
            Point(3,-0),
            Point(-0,3),
            Point(-1, 2),
            Point(-2, 1),
            Point(-3, 0),
            Point(-0, -3),
            Point(-1, -2),
            Point(-2, -1),
            Point(-3, -0),
        )

        val result = point.pointsWithManhattanDistance(manhattanDistance).toSet()
        expectedResult.forEach {
            assert(it in result)
        }
        assert(expectedResult.size == result.size)
    }

    @Test
    fun `move should move 1 point north given North`() {
        val result = Point(0, 0).move(CardinalDirection.North)
        assert(result == Point(0, -1))
    }

    @Test
    fun `move should move 1 point east given East`() {
        val result = Point(0, 0).move(CardinalDirection.East)
        assert(result == Point(1, 0))
    }

    @Test
    fun `move should move 1 point south given South`() {
        val result = Point(0, 0).move(CardinalDirection.South)
        assert(result == Point(0, 1))
    }

    @Test
    fun `move should move 1 point west given West`() {
        val result = Point(0, 0).move(CardinalDirection.West)
        assert(result == Point(-1, 0))
    }

    @Nested
    inner class CardinalDirectionTest {

        @Test
        fun `turning left from North should return West`() {
            val result = CardinalDirection.North.turn(TurnDirection.Left)
            assert(result == CardinalDirection.West)
        }

        @Test
        fun `turning right from North should return East`() {
            val result = CardinalDirection.North.turn(TurnDirection.Right)
            assert(result == CardinalDirection.East)
        }

        @Test
        fun `turning left from East should return North`() {
            val result = CardinalDirection.East.turn(TurnDirection.Left)
            assert(result == CardinalDirection.North)
        }

        @Test
        fun `turning right from East should return South`() {
            val result = CardinalDirection.East.turn(TurnDirection.Right)
            assert(result == CardinalDirection.South)
        }

        @Test
        fun `turning left from South should return East`() {
            val result = CardinalDirection.South.turn(TurnDirection.Left)
            assert(result == CardinalDirection.East)
        }

        @Test
        fun `turning right from South should return West`() {
            val result = CardinalDirection.South.turn(TurnDirection.Right)
            assert(result == CardinalDirection.West)
        }

        @Test
        fun `turning left from West should return South`() {
            val result = CardinalDirection.South.turn(TurnDirection.Left)
            assert(result == CardinalDirection.East)
        }

        @Test
        fun `turning right from West should return North`() {
            val result = CardinalDirection.South.turn(TurnDirection.Right)
            assert(result == CardinalDirection.West)
        }

        @ParameterizedTest
        @EnumSource(CardinalDirection::class)
        internal fun `turning left 4 times should return the original direction`(direction: CardinalDirection) {
            var newDirection = direction
            repeat(4) {
                newDirection = newDirection.turn(TurnDirection.Left)
            }
            assert(newDirection == direction)
        }

        @ParameterizedTest
        @EnumSource(CardinalDirection::class)
        internal fun `turning right 4 times should return the original direction`(direction: CardinalDirection) {
            var newDirection = direction
            repeat(4) {
                newDirection = newDirection.turn(TurnDirection.Right)
            }
            assert(newDirection == direction)
        }

        @ParameterizedTest
        @EnumSource(CardinalDirection::class)
        internal fun `turning left and then right should return the original direction`(direction: CardinalDirection) {
            val newDirection = direction.turn(TurnDirection.Left)
            val result = newDirection.turn(TurnDirection.Right)
            assert(direction != newDirection)
            assert(result == direction)
        }

        @ParameterizedTest
        @EnumSource(CardinalDirection::class)
        internal fun `turning right and then left should return the original direction`(direction: CardinalDirection) {
            val newDirection = direction.turn(TurnDirection.Right)
            val result = newDirection.turn(TurnDirection.Left)
            assert(direction != newDirection)
            assert(result == direction)
        }
    }
}