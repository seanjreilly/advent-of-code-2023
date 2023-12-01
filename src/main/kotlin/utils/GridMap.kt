package utils

abstract class GridMap<T>(protected val data : Array<Array<T>>, private val getNeighboursMethod: (Point) -> Collection<Point>) : Iterable<Point> {
    val height: Int = data.size
    val width: Int = data.first().size
    val bottomRightCorner = Point(width, height).northWest()

    init {
        //ensure the map is rectangular
        check(data.all { it.size == width }) {"every row must be the same size"}
    }

    operator fun get(point: Point): T = data[point.y][point.x]

    fun getNeighbours(point: Point): Collection<Point> {
        return getNeighboursMethod(point)
            .filter { contains(it) }
    }

    fun contains(point: Point): Boolean  = point.x in (0 until width) && point.y in (0 until height)

    override fun iterator() = iterator {
        for (x in 0 until width) {
            for (y in 0 until height) {
                yield(Point(x, y))
            }
        }
    }
}