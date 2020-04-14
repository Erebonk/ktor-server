import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

data class Author(val name: String, val website: String)
data class ToDo(var id: Int, val name: String, val description: String, val completed: Boolean)

fun main() {

    val toDoList = ArrayList<ToDo>();
    val jsonResponse = """{
            "id": 1,
            "task": "Pay waterbill",
            "description": "Pay water bill today",
        }"""

        val server = embeddedServer(Netty, 8080) {
            install(DefaultHeaders) {
                header("X-Developer", "Baeldung")
            }
            install(CallLogging) {
                level = Level.DEBUG
                filter { call -> call.request.path().startsWith("/todo") }
                filter { call -> call.request.path().startsWith("/author") }
            }
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }
            routing {
                get("/") {
                    call.respondText(jsonResponse, ContentType.Application.Json)
                }
                post {
                    val toDo = call.receive<ToDo>();
                    toDo.id = toDoList.size;
                    toDoList.add(toDo);
                    call.respond("Added")

                }
                delete("/{id}") {
                    call.respond(toDoList.removeAt(call.parameters["id"]!!.toInt()));
                }
                get("/{id}") {

                    call.respond(toDoList[call.parameters["id"]!!.toInt()]);
                }
                get {
                    call.respond(toDoList);
                }
            }
        }
        server.start(wait = true)
    }

