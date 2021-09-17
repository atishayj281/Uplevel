package android.example.uptoskills.mail

import javax.mail.internet.InternetAddress

import javax.mail.internet.MimeMessage

import android.widget.Toast
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*


class JavaMailAPI(val mContext: Context,val mEmail: String,val mSubject: String,val mMessage: String) {
    private var mSession: Session? = null
    fun sendMail() {
        GlobalScope.launch(Dispatchers.IO) {
            val props = Properties()

            //Configuring properties for gmail
            //If you are not using gmail you may need to change the values
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            //Creating a new session
            mSession = Session.getDefaultInstance(props,
                object : Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD)
                    }
                })
            try {
                //Creating MimeMessage object
                val mm = MimeMessage(mSession)

                //Setting sender address
                mm.setFrom(InternetAddress(Utils.EMAIL))
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, InternetAddress(mEmail))
                //Adding subject
                mm.subject = mSubject
                //Adding message
                mm.setText(mMessage)
                //Sending email
                Transport.send(mm)

//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);
            } catch (e: MessagingException) {
                e.printStackTrace()
            }
//            withContext(Dispatchers.Main) {
//                Toast.makeText(mContext, "Mail has been Sent", Toast.LENGTH_SHORT).show()
//            }
        }

    }
}