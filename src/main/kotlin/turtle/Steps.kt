package turtle

import common.Urumber
import common.UrumberDescription
import kotlin.test.assertEquals

class Steps {
    private val t = TurtleApp()

    @UrumberDescription("Set horizontal coordinate value")
    @Urumber("^Set horizontal (\\d+)\$")
    fun setHorizontal(x: Int){
        t.x = x
    }

    @UrumberDescription("Set vertical coordinate value")
    @Urumber("^Set vertical (\\d+)\$")
    fun setVertical(x: Int){
        t.y = x
    }

    @UrumberDescription("Calculate and validate turtle color")
    @Urumber("^Validate turtle color (WHITE|BLACK)\$")
    fun validateColor(color: String){
        assertEquals(color, t.getColor().name)
    }
}