package ru.ere

import io.ktor.application.Application
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.eq
import repo.Car
import repo.User
import ru.ere.service.KMongo.cars
import ru.ere.service.KMongo.users

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.mongoModule(testing: Boolean = false) {

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

}