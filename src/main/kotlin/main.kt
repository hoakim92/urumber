import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import apps.turtle.TurtleSteps
import apps.turtle.TurtleApp
import apps.userAdmin.UserAdminApp
import apps.userAdmin.UserAdminSteps
import common.*
import io.ktor.http.content.*
import java.io.File
import java.io.StringWriter

fun main(args: Array<String>) {
    val mf = DefaultMustacheFactory()
    fun getWriter(appName: String, app: Class<*>, steps: Class<*>, script: List<String>? = null): StringWriter {
        val w = StringWriter()
        val m = mf.compile(mf.getReader("templates/${appName}.html"), "apps/${appName}")
        m.execute(w, createPage(app, steps, script)).flush()
        return w
    }
    embeddedServer(Netty, 8080) {
        routing {
            static("static") {
                staticRootFolder= File("src\\main\\resources")
                files("js")
                files("css")
            }
            get("/apps/turtle") {
                call.respondText(
                    getWriter(
                        "turtle",
                        TurtleApp::class.java,
                        TurtleSteps()::class.java
                    ).buffer.toString(), ContentType.Text.Html
                )
            }
            post("/apps/turtle") {
                call.respondText(
                    getWriter(
                        "turtle",
                        TurtleApp::class.java,
                        TurtleSteps()::class.java,
                        call.receiveParameters().getAll("step[]")
                    ).buffer.toString(), ContentType.Text.Html
                )
            }
            get("/apps/userAdmin") {
                call.respondText(
                    getWriter(
                        "userAdmin",
                        UserAdminApp::class.java,
                        UserAdminSteps()::class.java
                    ).buffer.toString(), ContentType.Text.Html
                )
            }
            post("/apps/userAdmin") {
                call.respondText(
                    getWriter(
                        "userAdmin",
                        UserAdminApp::class.java,
                        UserAdminSteps()::class.java,
                        call.receiveParameters().getAll("step[]")
                    ).buffer.toString(), ContentType.Text.Html
                )
            }
        }
    }.start(wait = true)

}

fun createPage(app: Class<*>, steps: Class<*>, script: List<String>? = null) =
    Page(
        UrumberRunner.getAppDescription(app).asList().map { AppDescription(it) },
        UrumberRunner.getStepsViewAndDescription(steps).map { MethodView(it.first, it.second) },
        if (script != null) {
            UrumberRunner.runTest(
                script!!,
                steps.constructors[0].newInstance() as Any
            )
        } else {
            UrumberRunner.getDefaultSteps(steps).map { StepResult(it) }
        }
    )

