package com.pan.foodbox.mailObject

import android.content.Context
import io.reactivex.Completable
import java.util.*
import javax.mail.Message.RecipientType.TO
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object Mailer {

    fun sendMail(context: Context, email: String, subject: String, message: String): Completable {

        return Completable.create { emitter ->

            val props = Properties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "993")

            //Creating a Session
            val session = javax.mail.Session.getDefaultInstance(props, object :
                javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Config.Email, Config.pass)
                }
            })

            try {

                MimeMessage(session).let { mime ->
                    mime.setFrom(InternetAddress(Config.Email))
                    //Adding receiver
                    mime.addRecipient(TO, InternetAddress(email))
                    //Adding subject
                    mime.setSubject(subject)
                    //Adding message
                    mime.setText(message)
                    //Send mail
                    Transport.send(mime)
                }
            }
            catch (e:MessagingException){
                emitter.onError(e)

            }
            emitter.onComplete()
        }
    }
}