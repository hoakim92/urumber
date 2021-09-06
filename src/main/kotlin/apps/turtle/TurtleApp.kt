package apps.turtle

import common.Urumber

@Urumber(
    appDescription = [
        "Turtle has two coordinates horizontal(x) and vertical(y)",
        "Turtle has color which calculated from coordinates by rule:",
        "if (x+y) mod 2 == 0 then WHITE",
        "if (x+y) mod 2 != 0 then BLACK"
    ]
)
class TurtleApp {
    enum class COLOR { BLACK, WHITE }

    var x: Int = 0
    var y: Int = 0

    fun run() = (x + y) % 2 == 0
    fun getColor(): COLOR {
        if (x == 0) {
            return COLOR.BLACK
        } else {
            return if ((x + y) % 2 == 0) {
                COLOR.WHITE
            } else {
                COLOR.BLACK
            }
        }
    }
}