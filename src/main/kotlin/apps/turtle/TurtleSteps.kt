package apps.turtle

import common.Urumber
import kotlin.test.assertEquals

@Urumber(
    defaultSteps = [
        "Set horizontal 5",
        "Set vertical 5",
        "Validate turtle color BLACK"
    ]
)
class TurtleSteps {
    private val app = TurtleApp()

    @Urumber(
        stepRegExp = "^Set horizontal (\\d+)\$",
        stepDescription = "Set horizontal coordinate value",
        stepView = "Set horizontal Int"
    )
    fun setHorizontal(x: Int) {
        app.x = x
    }

    @Urumber(
        stepRegExp = "^Set vertical (\\d+)\$",
        stepDescription = "Set vertical coordinate value",
        stepView = "Set vertical Int"
    )
    fun setVertical(x: Int) {
        app.y = x
    }

    @Urumber(
        stepRegExp = "^Validate turtle color (WHITE|BLACK)\$",
        stepDescription = "Calculate and validate turtle color",
        stepView = "Validate turtle color (WHITE|BLACK)"
    )
    fun validateColor(color: String) {
        assertEquals(color, app.getColor().name)
    }
}