package android.example.uptoskills

import android.content.Context
import android.example.uptoskills.models.Users
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

class Resume(val user: Users, val context: Context) {
    private lateinit var pdfDocument: PdfDocument
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var file: File
    private lateinit var bitmap: Bitmap
    private lateinit var scaleBitmap: Bitmap



    fun createResume() {
        pdfDocument = PdfDocument()
        paint = Paint()

        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(1300, 2400, 1).create()
        val page1 = pdfDocument.startPage(pageInfo)
        canvas = page1.canvas

        paint.textSize = 60f
        paint.textAlign = Paint.Align.CENTER

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Resume", (pageInfo.pageWidth/2).toFloat(), 80f, paint)
        paint.color = ContextCompat.getColor(context, R.color.com_facebook_messenger_blue)
        paint.strokeWidth = 10f
        canvas.drawLine((pageInfo.pageWidth/2).toFloat() - 190f, 100f, (pageInfo.pageWidth/2).toFloat() + 170f, 100f, paint)

        val x_heading = 100f

        // Name
        paint.textSize = 50f
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(user.full_name, x_heading, 160f, paint)

        paint.textSize = 25f
        paint.color = ContextCompat.getColor(context, R.color.darkGray)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("340, South Krishnapuri, Muzaffarnagar", x_heading, 200f, paint)
        canvas.drawText(user.mobileNo, x_heading, 240f, paint)
        canvas.drawText(user.email, x_heading, 280f, paint)
        paint.strokeWidth = 3f
        canvas.drawLine(x_heading, 300f, pageInfo.pageWidth - x_heading, 300f, paint)

        // Education
        paint.textSize = 35f
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Education: ", x_heading, 360f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 30f
        canvas.drawText(user.education, x_heading + 200f, 360f, paint)
        paint.color = ContextCompat.getColor(context, R.color.darkGray)
        paint.textSize = 28f
        canvas.drawText(user.college_name, x_heading + 200f, 400f, paint)

        

        pdfDocument.finishPage(page1)
        file = File(context.externalCacheDir!!.absolutePath, "/${user.id}.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, file.absolutePath, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.message?.let { Log.e("Certificate Error", it) }
        }

        pdfDocument.close()
    }
}