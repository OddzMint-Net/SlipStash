package com.oddzmint.slipstash.data.work

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class ReceiptCompressor @Inject constructor() {

    /** Writes a compressed JPEG to [target]. Returns false if the source can't be read. */
    fun compress(sourcePath: String, target: File): Boolean {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(sourcePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return false

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / sample > MAX_SIDE * 2) sample *= 2
        val decoded = BitmapFactory.decodeFile(
            sourcePath, BitmapFactory.Options().apply { inSampleSize = sample }
        ) ?: return false

        val degrees = when (
            ExifInterface(sourcePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val scale = min(1f, MAX_SIDE.toFloat() / max(decoded.width, decoded.height))
        val matrix = Matrix().apply {
            postRotate(degrees)
            postScale(scale, scale)
        }
        val bitmap = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)

        val out = ByteArrayOutputStream()
        var quality = 85
        do {
            out.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            quality -= 10
        } while (out.size() > MAX_BYTES && quality >= 35)

        target.parentFile?.mkdirs()
        target.writeBytes(out.toByteArray())
        return true
    }

    private companion object {
        const val MAX_SIDE = 1280
        const val MAX_BYTES = 300_000
    }
}