package com.fadhil.submissionstoryapp_akhir.pref

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import com.fadhil.submissionstoryapp_akhir.R
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun String.isEmailValid(): Boolean  {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

object Utils {
    private const val FILENAME = "dd-MMM-yyyy"

    val timeStamp: String = SimpleDateFormat(
        FILENAME,
        Locale.US
    ).format(System.currentTimeMillis())

    fun String.formatDate(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US)
        val unformattedDate = inputFormat.parse(this) as Date
        return outputFormat.format(unformattedDate)
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    fun uriToFile(img: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTemp(context)

        val inputStream = contentResolver.openInputStream(img) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun reduceFileSize(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun createTemp(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }
}


private const val timesstampformat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

fun getTimeLineUploaded(context: Context, timeStamp: String?): String {
    val currentTime = getCurrentDate()
    val uploadTime = timeStamp(timeStamp.toString())
    val diff: Long = currentTime.time - uploadTime.time
    val second = diff / 1000
    val minutes = second / 60
    val hours = minutes / 60
    val days = hours / 24
    val label = when (minutes.toInt()) {
        0 -> "$second ${context.getString(R.string.second_ago)}"
        in 1..59 -> "$minutes ${context.getString(R.string.minutes_ago)}"
        in 60..1440 -> "$hours ${context.getString(R.string.hours_ago)}"
        else -> "$days ${context.getString(R.string.days_ago)}"
    }
    return label
}

fun timeStamp(timeStamp: String): Date =
    try {
        val formatter = SimpleDateFormat(timesstampformat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(timeStamp) as Date
    } catch (e: ParseException) {
        getCurrentDate()
    }

fun getCurrentDate(): Date = Date()

