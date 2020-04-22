package ru.ere.service

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import repo.Car
import repo.User

object KMongo {
    val kmongoClient = KMongo.createClient().coroutine
    val database = kmongoClient.getDatabase("template")
    val cars = database.getCollection<Car>()
    val users = database.getCollection<User>()
}