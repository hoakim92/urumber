package turtle

class TurtleApp {
    enum class COLOR { BLACK, WHITE }

    var x: Int = 0
    var y: Int = 0

    fun run() = (x + y) % 2 == 0
    fun getColor(): COLOR {
        return if ((x + y) % 2 == 0) {
            COLOR.WHITE
        } else {
            COLOR.BLACK
        }
    }
}