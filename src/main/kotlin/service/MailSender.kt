package service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

suspend fun getReceiverAddressFromDatabase(): String {
    delay(1000)
    return "coroutine@kotlin.org"
}

suspend fun sendEmail(r: String, msg: String): Boolean {
    delay(2000)
    println("Sent '$msg' to $r")
    return true
}

suspend fun sendEmailSuspending(): Boolean {
    val msg = GlobalScope.async {
        delay(500)
        "The message content"
    }
    val recipient = GlobalScope.async { getReceiverAddressFromDatabase() }
    println("Waiting for email data")

    val sendStatus = GlobalScope.async {
        sendEmail(recipient.await(), msg.await())
    }
    return sendStatus.await()
}