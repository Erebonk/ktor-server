package controller

import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import repo.Car
import repo.User
import java.text.DateFormat

const val CARS_ENDPOINT = "/cars"
const val USERS_ENDPOINT = "/users"

fun Application.main() {

    val kmongoClient = KMongo.createClient().coroutine
    val database = kmongoClient.getDatabase("template")
    val cars = database.getCollection<Car>()
    val users = database.getCollection<User>()

    runBlocking {
        if (cars.find().toList().isEmpty()) {
            val user = User("Max", "Fedrish","1eqw@ga.ru")
            users.insertOne(user)
            val savedUser = users.findOne(User::name eq "Max", User::surname eq "Fedrish")
            val car = Car("BMW","BMW",
                "550Li", "131fdw1", "E222EE199", savedUser)
            cars.insertOne(car)
        }
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        call.request.headers
                .forEach {
                    name, values ->
                    println("$name and ${values.joinToString()}")
                }
    }
    install(CallLogging) // logger
    install(DefaultHeaders) // def header for response
    install(ContentNegotiation) { // for json
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText { "Hello KTor!" }
        }

        get(CARS_ENDPOINT) {
                log.info("Get all cars entities")
                call.respond(HttpStatusCode.OK, cars.find().toList())
        }

        get(USERS_ENDPOINT) {
            log.info("Get all users entities")
            call.respond(HttpStatusCode.OK, users.find().toList())
        }

        get("$CARS_ENDPOINT/{number}") {
            log.info("Find card by number")
            val number = call.parameters["number"]
            if (number != null) {
                val car = cars.findOne(Car::number eq number)
                if (car != null) call.respond(HttpStatusCode.OK, car)
                else call.respond(HttpStatusCode.NotFound, "Car not found!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Where your id?")
            }
        }

        get("$USERS_ENDPOINT/{email}") {
            log.info("Find user by email")
            val email = call.parameters["email"]
            if (email != null) {
                val user = users.findOne("{email: '$email'}")
                if (user != null) call.respond(HttpStatusCode.OK, user)
                else call.respond(HttpStatusCode.NotFound, "User not found!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Where your email?")
            }
        }

        post(CARS_ENDPOINT) {
            log.info("add new car")
            val receive = call.receive<Car>()
            cars.insertOne(receive)
            call.respond(HttpStatusCode.Accepted, "accepted")
        }

        post(USERS_ENDPOINT) {
            log.info("add new user")
            val receive = call.receive<User>()
            users.insertOne(receive)
            call.respond(HttpStatusCode.Accepted, "accepted")
        }

    }

}