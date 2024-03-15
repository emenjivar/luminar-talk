package com.emenjivar.luminar.screen.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ext.settingsIntent
import com.emenjivar.luminar.ui.components.CustomDialog
import com.emenjivar.luminar.ui.components.CustomDialogAction
import com.emenjivar.luminar.ui.components.rememberCustomDialogController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel()
) {
    CameraScreenContent(uiState = viewModel.state)
}

@OptIn(ExperimentalPermissionsApi::class)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraScreenContent(
    uiState: CameraUiState
) {
    // Compose variables
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Controllers
    val dialogController = rememberCustomDialogController()
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { isGranted ->
            if (!isGranted) {
                dialogController.show()
            }
        }
    )

    // Flows
    val morseCharacter by uiState.morseCharacter.collectAsState()
    val word by uiState.word.collectAsState()
    val debugMorse by uiState.debugMorse.collectAsState()

    // Remembered values
    val previewView = remember {
        PreviewView(context).apply {
            this.scaleType = PreviewView.ScaleType.FIT_CENTER
        }
    }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = remember {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }
    val executor = remember {
        ContextCompat.getMainExecutor(context)
    }
    var imageWithFilter by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val isFlashTurnOn = remember { mutableStateOf(false) }

    // SideEffects
    LaunchedEffect(isFlashTurnOn) {
        snapshotFlow { isFlashTurnOn.value }
            .distinctUntilChanged()
            .onEach { isTurnOn ->
                uiState.addFlashState(isTurnOn)
            }.launchIn(this)
    }

    LaunchedEffect(morseCharacter) {
        when (morseCharacter) {
            MorseCharacter.DIT, MorseCharacter.DAH -> {
                delay(CameraViewModel.SPACE_LETTER)
                uiState.finishLetter()
            }
            MorseCharacter.LETTER_SPACE -> {
                delay(CameraViewModel.SPACE_WORD - CameraViewModel.SPACE_LETTER)
                uiState.finishWord()
            }
            else -> {}
        }
    }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(executor, CustomImageAnalyzer(
                    onDrawImage = { isTurnOn, bitmap ->
                        isFlashTurnOn.value = isTurnOn
                        imageWithFilter = bitmap
                    }
                ))
            }
    }
    var camera by remember { mutableStateOf<Camera?>(null) }

    // States
    val preview = Preview.Builder().build()

    // Launched
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
        }
    }

    CameraScreenLayout(
        rawCameraPreview = { modifier ->
            AndroidView(
                modifier = modifier,
                factory = { previewView }
            )
        },
        filteredCameraPreview = { modifier ->
            AndroidView(
                modifier = modifier,
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                },
                update = { view ->
                    imageWithFilter?.let { bitmap ->
                        view.setImageBitmap(bitmap)
                    }
                }
            )
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    modifier = Modifier
                        .background(Color.Black),
                    text = word.orEmpty(),
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .background(Color.Black),
                    text = debugMorse,
                    color = Color.White
                )
            }
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = uiState.clearText
            ) {
                Text(text = "Clear")
            }
        }
    )

    CustomDialog(
        controller = dialogController,
        title = stringResource(R.string.camera_dialog_access_title),
        description = stringResource(R.string.camera_dialog_access_description),
        confirmAction = CustomDialogAction(
            text = stringResource(R.string.button_settings),
            onClick = {
                context.startActivity(context.settingsIntent)
            }
        ),
        dismissAction = CustomDialogAction(
            text = stringResource(R.string.button_close),
            onClick = {}
        )
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
