import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import repo.Car
import repo.KRepository
import java.text.DateFormat

const val CARS_ENDPOINT = "/cars"

fun Application.main() {
    install(DefaultHeaders)

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText { "OK" }
        }

        get(CARS_ENDPOINT) {
            errorAware {
                log.info("Get all cars entities")
                call.respond(KRepository::getAll)
            }
        }

        get("$CARS_ENDPOINT/{id}") {
            errorAware {
                val id = call.parameters.get("id") ?: throw IllegalArgumentException("Parameter id not found")
                log.info("Get Person entity with id=$id")
                val car = KRepository.get(id) ?: throw IllegalArgumentException("Car not found!")
                call.respond(car)
            }
        }

        post(CARS_ENDPOINT) {
            errorAware {
                val receive = call.receive<Car>()
                println("Received Post Request: $receive")
                val user = KRepository.add(receive) ?: throw IllegalArgumentException("Parameter id not found")
                val job = GlobalScope.launch {
                    sendEmailSuspending()
                    println("Email sent successfully.")
                }
                job.join()
                println("Finished")
                call.respond("cars was added.")
            }
        }

    }

}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText(
            """{"error":"$e"}""",
            ContentType.parse("application/json"),
            HttpStatusCode.InternalServerError
        )
        null
    }
}