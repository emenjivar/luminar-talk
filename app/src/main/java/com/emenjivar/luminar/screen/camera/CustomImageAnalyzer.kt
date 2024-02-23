package com.emenjivar.luminar.screen.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class CustomImageAnalyzer(
    private val onDrawImage: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private val originalMat = Mat()

    override fun analyze(image: ImageProxy) {
        // Convert cameraX image to openCV mat
        val bitmap = image.image?.toBitmap()
        Utils.bitmapToMat(bitmap, originalMat)

        // Convert to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_RGBA2GRAY)

        // Rotate the image
        val rotated = Mat()
        Core.rotate(grayMat, rotated, Core.ROTATE_90_CLOCKWISE)

        val finalBitmap = Bitmap.createBitmap(
            rotated.width(),
            rotated.height(),
            Bitmap.Config.ARGB_8888
        )

        // Put on the bitmap
        Utils.matToBitmap(rotated, finalBitmap)

        image.close()
        originalMat.release()
        onDrawImage(finalBitmap)
    }

    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // YU
        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), BITMAP_QUALITY, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    companion object {
        private const val BITMAP_QUALITY = 100
    }
}
