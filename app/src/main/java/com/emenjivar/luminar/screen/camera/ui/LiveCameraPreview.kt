package com.emenjivar.luminar.screen.camera.ui

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Composable that displays a camera preview using the given [previewView].
 *
 * @param previewView The preview used to display the camera preview.
 * @param analyzer The analyzed used for processing the camera frames.
 * @param permissionState Used for request needed permissions from outside.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveCameraPreview(
    previewView: PreviewView,
    analyzer: Analyzer,
    permissionState: PermissionState,
    modifier: Modifier = Modifier,
    controller: LiveCameraPreviewController = rememberLiveCameraController()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isTorchEnabled by controller.isTorchEnabled.collectAsStateWithLifecycle()
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = remember {
        CameraSelector.Builder()
            // TODO: Some devices does not have lens facing, thar scenario makes crash the app
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }
    val executor = remember {
        ContextCompat.getMainExecutor(context)
    }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(executor, analyzer)
            }
    }
    var camera by remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            camera = startCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector,
                preview = preview,
                previewView = previewView,
                imageCapture = imageCapture,
                imageAnalysis = imageAnalysis
            )

            // Send a signal to verify the flashlight availability outside this composable
            val hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() ?: false
            controller.setFlashTorchAvailability(hasFlashUnit)
        }
    }

    LaunchedEffect(isTorchEnabled) {
        if (camera?.cameraInfo?.hasFlashUnit() != true) {
            return@LaunchedEffect
        }
        camera?.cameraControl?.enableTorch(isTorchEnabled)
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )
}

private suspend fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview,
    previewView: PreviewView,
    imageCapture: ImageCapture,
    imageAnalysis: ImageAnalysis
): Camera {
    val cameraProvider = context.getCameraProvider()
    cameraProvider.unbindAll()
    val camera = cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageAnalysis,
        imageCapture
    )
    preview.setSurfaceProvider(previewView.surfaceProvider)
    return camera
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val provider = cameraProviderFuture.get()
        cameraProviderFuture.addListener({
            continuation.resume(provider)
        }, ContextCompat.getMainExecutor(this))
    }

/**
 * Used for control the camera instance.
 */
class LiveCameraPreviewController {
    private val _isTorchEnabled = MutableStateFlow(false)

    /**
     * Indicates the status of the torch.
     */
    val isTorchEnabled = _isTorchEnabled.asStateFlow()

    private val _hasFlashTorchAvailable = MutableStateFlow<Boolean?>(null)

    /**
     * A state flow that emits whether the flash torch is available on the device.
     */
    val hasFlashTorchAvailable = _hasFlashTorchAvailable.asStateFlow()

    /**
     * Enable the torch (flashlight) on the device.
     */
    fun turnOnTorch() {
        _isTorchEnabled.update { true }
    }

    /**
     * Disable the torch (flashlight) on the device.
     */
    fun turnOffTorch() {
        _isTorchEnabled.update { false }
    }

    fun setFlashTorchAvailability(value: Boolean) {
        _hasFlashTorchAvailable.update { value }
    }
}

@Composable
fun rememberLiveCameraController() = remember {
    LiveCameraPreviewController()
}
