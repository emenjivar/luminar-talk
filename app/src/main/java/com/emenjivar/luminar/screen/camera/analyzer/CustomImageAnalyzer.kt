package com.emenjivar.luminar.screen.camera.analyzer

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.features2d.Features2d
import org.opencv.features2d.SimpleBlobDetector
import org.opencv.features2d.SimpleBlobDetector_Params
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.FILLED
import org.opencv.imgproc.Imgproc.FONT_HERSHEY_COMPLEX_SMALL
import org.opencv.imgproc.Imgproc.THRESH_BINARY

class CustomImageAnalyzer(
    private val blobParameters: SimpleBlobDetector_Params,
    private val onDrawImage: (
        isFlashTurnOn: Boolean,
        debug: Bitmap
    ) -> Unit
) : ImageAnalysis.Analyzer {

    private val originalMat = Mat()

    @Suppress("MagicNumber")
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

        // Blurred the image
        val blurMat = Mat()
        Imgproc.GaussianBlur(
            grayMat,
            blurMat,
            Size(15.0, 15.0),
            0.0,
            0.0
        )

        // Get a dynamic threshold value using the brighter pixel on the screen
        val minMaxResult = Core.minMaxLoc(blurMat)
        val thresholdMat = Mat()
        Imgproc.threshold(
            blurMat,
            thresholdMat,
            (minMaxResult.maxVal - 10.0).coerceAtLeast(MIN_THRESHOLD),
            255.0,
            THRESH_BINARY
        )


        // Blows are extracted easy from negative images
        val inverseMath = Mat()
        val keyPoints = MatOfKeyPoint()
        val blobDetector = SimpleBlobDetector.create(blobParameters)
        Core.bitwise_not(thresholdMat, inverseMath)
        blobDetector.detect(inverseMath, keyPoints)

        // Draw blobs in screen
        val blobMat = Mat()
        Features2d.drawKeypoints(
            rotated, keyPoints, blobMat, Scalar(0.0, 255.0, 0.0),
            Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS
        )

        // Print debug data on screen
        Imgproc.rectangle(
            blobMat,
            Rect(Point(0.0, 0.0), Point(rotated.width().toDouble(), 20.0)),
            Scalar.all(50.0),
            FILLED
        )

        val isFlashTurnOn = !keyPoints.empty()
        Imgproc.putText(
            blobMat,
            "flashlight: ${if (isFlashTurnOn) "ON" else "OFF"}",
            Point(10.0, 10.0),
            FONT_HERSHEY_COMPLEX_SMALL,
            FONT_SCALE,
            Scalar(0.0, 255.0, 0.0)
        )

        val outputBitmap = Bitmap.createBitmap(
            blobMat.width(),
            blobMat.height(),
            Bitmap.Config.ARGB_8888
        )

        // Put on the bitmap
        Utils.matToBitmap(blobMat, outputBitmap)

        image.close()
        originalMat.release()
        onDrawImage(isFlashTurnOn, outputBitmap)
    }

    companion object {
        private const val FONT_SCALE = 0.6
        private const val MIN_THRESHOLD = 158.0
    }
}
