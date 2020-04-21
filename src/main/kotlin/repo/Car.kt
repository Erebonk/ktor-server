package repo

data class Car(
    val vendor: String,
    val name: String,
    val model: String,
    val vin: String,
    val number: String,
    val user: User? = null
)