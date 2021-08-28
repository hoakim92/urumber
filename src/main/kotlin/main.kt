import com.github.mustachejava.DefaultMustacheFactory
import common.RESULT
import common.StepResult
import common.Urumber
import common.UrumberDescription
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
    val methods = getRegExpsAndDescription(Steps::class.java).map { MethodView(it.first, it.second) }
    embeddedServer(Netty, 8080) {
        routing {
            get("/turtle") {
                val m = mf.compile(mf.getReader("templates/turtle.html"), "turtle")
                val w = StringWriter()
                m.execute(w, Page(methods)).flush()
                call.respondText(w.buffer.toString(), ContentType.Text.Html)
            }
            post("/turtle") {
                val m = mf.compile(mf.getReader("templates/turtle.html"), "turtle")
                val w = StringWriter()
                val fscript = call.receiveParameters()["fscript"]
                if (fscript != null) {
                    m.execute(
                        w, Page(
                            methods,
                            fscript,
                            runTest(
                                fscript!!,
                                turtle.Steps() as Any
                            )
                        )
                    ).flush()
                } else {
                    m.execute(w, Page(methods)).flush()
                }
                call.respondText(w.buffer.toString(), ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}

data class Page(
    val methods: List<MethodView>,
    val fscript: String =
        """GIVEN Set horizontal 5
GIVEN Set vertical 5
THEN Validate turtle color BLACK""".trimIndent(),
    val testResults: List<StepResult> = emptyList()
)

data class MethodView(val regexp: String, val description: String)

fun splitSteps(text: String) = text.split(Regex("(GIVEN|THEN|WHEN)")).filter { it.isNotEmpty() }.map { it.trim() }

fun runTest(steps: String, obj: Any): List<StepResult> {
    return runSteps(splitSteps(steps), getMethodsByRegExp(obj.javaClass), obj)
}

fun runSteps(steps: List<String>, methods: List<Pair<String, Method>>, obj: Any) =
    steps.map {
        runStep(it, methods, obj)
    }


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

fun getMethodsByRegExp(clazz: Class<*>) = clazz.methods.filter { it.isAnnotationPresent(Urumber::class.java) }
    .map { it.getAnnotation(Urumber::class.java).regExp to it }

fun getRegExpsAndDescription(clazz: Class<*>) = clazz.methods.filter { it.isAnnotationPresent(Urumber::class.java) }
    .map { it.getAnnotation(Urumber::class.java).regExp to it.getAnnotation(UrumberDescription::class.java).description}
