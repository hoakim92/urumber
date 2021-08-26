package turtle

import common.Urumber
import kotlin.test.assertEquals

class Steps {
    private val t = TurtleApp()

    @Urumber("^Set horizontal (\\d+)\$")
    fun setHorizontal(x: Int){
        t.x = x
    }

    @Urumber("^Set vertical (\\d+)\$")
    fun setVertical(x: Int){
        t.y = x
    }

    @Urumber("^Validate turtle color (WHITE|BLACK)\$")
    fun validateColor(color: String){
        assertEquals(t.getColor().name, color)
    }
}