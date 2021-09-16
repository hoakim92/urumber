import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.DefaultMustacheVisitor
import com.sun.jdi.InvocationException
import common.RESULT
import common.StepResult
import common.Urumber
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import turtle.Steps
import java.io.StringWriter
import java.lang.UnsupportedOperationException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

fun main(args: Array<String>) {
    val mf = DefaultMustacheFactory()
    embeddedServer(Netty, 8080) {
        routing {
            get("/turtle") {
                val m = mf.compile(mf.getReader("templates/hello.html"), "hello")
                val w = StringWriter()
                m.execute(w, "null").flush()
                call.respondText(w.buffer.toString(), ContentType.Text.Html)
            }
            get("/turtle/methods") {
                call.respondText(getMethods(Steps::class.java).map { it.first }.joinToString(", ", "{", "}"))
            }
            post("/turtle/runTest") {
                call.respondText(
                    runTest(
                        call.receiveText(),
                        turtle.Steps() as Any
                    )
                )
            }
        }
    }.start(wait = true)

}

fun splitSteps(text: String) = text.split(Regex("(GIVEN|THEN|WHEN)")).filter { it.isNotEmpty() }.map { it.trim() }

fun runTest(steps: String, obj: Any): String {
    return runSteps(splitSteps(steps), getMethods(obj.javaClass), obj)
}

fun runSteps(steps: List<String>, methods: List<Pair<String, Method>>, obj: Any) =
    steps.map {
        runStep(it, methods, obj)
    }.joinToString(",", "[", "]")


fun runStep(step: String, methods: List<Pair<String, Method>>, obj: Any): StepResult {
    try {
        if (methods.find { Regex(it.first).matches(step) } != null) {
            val regExpAndMethods = methods.find { Regex(it.first).matches(step) }!!
            val groupValues = getArgsFromCommand(step, regExpAndMethods.first)
            val arguments = groupValues.subList(1, groupValues.size)
            runMethod(regExpAndMethods.second, arguments, obj)
        } else {
            throw UnsupportedOperationException("NO SUCH METHOD")
        }
    } catch (e: InvocationTargetException) {
        return StepResult(step, RESULT.FAILED, e.cause!!.toString())
    } catch (e: Exception) {
        return StepResult(step, RESULT.FAILED, e.toString())
    }
    return StepResult(step, RESULT.PASSED)
}

fun runMethod(method: Method, arguments: List<String>, obj: Any) {
    val argumentsAfterCast = arguments.mapIndexed { index, s ->
        when (method.parameters[index].parameterizedType.typeName) {
            "int" -> s.toInt()
            "java.lang.String" -> s
            else -> throw Exception("Unsupported Argument")
        }
    }.toTypedArray()
    method.invoke(obj, *argumentsAfterCast)
}

fun getArgsFromCommand(step: String, regExp: String) = Regex(regExp).matchEntire(step)!!.groupValues

fun getMethods(clazz: Class<*>) = clazz.methods.filter { it.isAnnotationPresent(Urumber::class.java) }
    .map { it.getAnnotation(Urumber::class.java).regExp to it }
