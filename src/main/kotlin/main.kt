import turtle.Steps
import turtle.Urumber
import java.lang.reflect.Method

fun main(args: Array<String>) {
    resolveMethodByRegExp(
        splitSteps(
            """
                GIVEN Set horizontal 5 
                GIVEN Set vertical 5 
                THEN Validate turtle color BLACK
            """.trimIndent()
        ), getMethods()
    )
}

fun splitSteps(text: String) = text.split(Regex("(GIVEN|THEN|WHEN)")).filter { it.isNotEmpty() }.map { it.trim() }

fun resolveMethodByRegExp(steps: List<String>, methods: List<Pair<String, Method>>) {
    steps.forEach {
        resolveMethodByRegExp(it, methods)
    }
}

fun resolveMethodByRegExp(step: String, methods: List<Pair<String, Method>>) {
    val method = methods.find { Regex(it.first).matches(step) }!!
    val groupValues = getArgsFromCommand(step, method.first)
    val arguments = groupValues.subList(1, groupValues.size)
    runMethod(arguments, method.second)
}

fun runMethod(arguments: List<String>, method: Method) {
    val argumentsAfterCast = arguments.mapIndexed { index, s ->
        when (method.parameters[index].parameterizedType.typeName) {
            "int" -> s.toInt()
            "java.lang.String" -> s
            else -> throw Exception("Unsupported Argument")
        }
    }.toTypedArray()
    method.invoke(Steps(), *argumentsAfterCast)
}

fun getArgsFromCommand(step: String, regExp: String) = Regex(regExp).matchEntire(step)!!.groupValues

fun getMethods() = Steps::class.java.methods.filter { it.isAnnotationPresent(Urumber::class.java) }
    .map { it.getAnnotation(Urumber::class.java).regExp to it }
