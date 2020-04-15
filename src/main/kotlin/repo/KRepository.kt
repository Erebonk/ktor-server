package repo

object KRepository {

    val cars = mutableListOf(Car(1, "BMW", "BMW", "750Li",
        "dfg1231", "C222CC777", 1),
        Car(2, "Mersedes", "Mersedes cli 200", "cli 220",
            "1231dsf", "E222KG123", 2))

    val users = mutableListOf(User(1, "Max", "Shaker", "emasd123@gmail.com"))

    fun getAll() = cars

    fun get(id: String) : Car? = cars.find { car -> car.id.toString() == id }

    fun add(c: Car): User? {
        if (cars.contains(c))
            return users.find { c.userId == it.id }
        throw NoSuchElementException("There's not the user here.")
    }


}