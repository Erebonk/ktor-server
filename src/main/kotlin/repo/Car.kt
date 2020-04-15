package repo

data class Car(
    val id: Long,
    val vendor: String,
    val name: String,
    val model: String,
    val vin: String,
    val number: String,
    val userId: Long
)