package com.emenjivar.luminar.screen.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.FILLED
import org.opencv.imgproc.Imgproc.FONT_HERSHEY_COMPLEX_SMALL
import org.opencv.imgproc.Imgproc.THRESH_BINARY

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class CustomImageAnalyzer(
    private val onDrawImage: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private val originalMat = Mat()

    override fun analyze(image: ImageProxy) {
        // Convert cameraX image to openCV mat
        val bitmap = image.toBitmap()
        Utils.bitmapToMat(bitmap, originalMat)

        // Rotate the image
        val rotated = Mat()
        Core.rotate(originalMat, rotated, Core.ROTATE_90_CLOCKWISE)

        // Convert to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(rotated, grayMat, Imgproc.COLOR_RGBA2GRAY)

        val blurMat = Mat()
        Imgproc.GaussianBlur(grayMat, blurMat, Size(15.0, 15.0), 0.0, 0.0)

        val minMaxResult = Core.minMaxLoc(blurMat)
        val thresholdMat = Mat()
        Imgproc.threshold(blurMat, thresholdMat, minMaxResult.maxVal - 20.0, 255.0, THRESH_BINARY)

        val totalPixels = thresholdMat.size().area()
        val lightPixels = Core.countNonZero(thresholdMat)
        val lightPercentage = lightPixels * 100 / totalPixels

        Imgproc.rectangle(
            rotated,
            Rect(Point(0.0, 0.0), Point(rotated.width().toDouble(), 20.0)),
            Scalar.all(50.0),
            FILLED
        )

        Imgproc.putText(
            rotated,
            "light percentage: $lightPercentage",
            Point(10.0, 10.0),
            FONT_HERSHEY_COMPLEX_SMALL,
            0.6,
            Scalar(0.0, 255.0, 0.0)
        )

        Imgproc.drawMarker(
            rotated,
            minMaxResult.maxLoc,
            Scalar(0.0, 0.0, 255.0)
        )

        val contours = mutableListOf<MatOfPoint>()
        val hierarchyMat = Mat()
        Imgproc.findContours(
            thresholdMat,
            contours,
            hierarchyMat,
            Imgproc.RETR_LIST,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        Imgproc.drawContours(rotated, contours, -1, Scalar(0.0, 255.0, 0.0))

        val outputBitmap = Bitmap.createBitmap(
            rotated.width(),
            rotated.height(),
            Bitmap.Config.ARGB_8888
        )

        // Put on the bitmap
        Utils.matToBitmap(rotated, outputBitmap)

        image.close()
        originalMat.release()
        onDrawImage(outputBitmap)
    }
}
