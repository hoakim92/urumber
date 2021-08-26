package turtle

import kotlin.test.assertEquals

class Steps {
    private val t = TurtleApp()

    @Urumber("^Set horizontal (\\d+)\$")
    fun setHorizontal(x: Int){
        print("Set horizontal $x")
        t.x = x
        println(" PASSED")
    }

    @Urumber("^Set vertical (\\d+)\$")
    fun setVertical(x: Int){
        print("Set vertical $x")
        t.x = x
        println(" PASSED")
    }

    @Urumber("^Validate turtle color (WHITE|BLACK)\$")
    fun validateColor(color: String){
        print("Validate turtle color $color")
        assertEquals(t.getColor().name, color)
        println(" PASSED")
    }
}


annotation class Urumber(val regExp: String)