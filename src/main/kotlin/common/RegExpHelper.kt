package common

class RegExpHelper {
    companion object{
        fun getArgsFromCommand(step: String, regExp: String) = Regex(regExp).matchEntire(step)!!.groupValues

        fun getMethodsByRegExp(clazz: Class<*>) = clazz.methods.filter { it.isAnnotationPresent(Urumber::class.java) }
            .map { it.getAnnotation(Urumber::class.java).stepRegExp to it }
    }
}