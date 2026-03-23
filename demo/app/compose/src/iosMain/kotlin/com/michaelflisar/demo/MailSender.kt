package com.michaelflisar.demo

import MailSenderHelper.MailSenderHelper

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
object MailSender {

    fun sendMail(
        receiver: String,
        attachments: List<String>
    ) {
        try {
            // Die generierte Methode heißt sendMailWithReceiver
            MailSenderHelper().sendMailWithReceiver(receiver, attachments)
        } catch (e: Throwable) {
            // Fehlerbehandlung, z.B. Logging
            println(e.message)
        }
    }
}
