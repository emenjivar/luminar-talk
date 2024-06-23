package com.emenjivar.luminar.screen.camera

import android.Manifest
import android.graphics.Bitmap
import android.util.Range
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ext.isInProgress
import com.emenjivar.luminar.ext.settingsIntent
import com.emenjivar.luminar.screen.camera.analyzer.CustomImageAnalyzer
import com.emenjivar.luminar.screen.camera.ui.DualCameraPreview
import com.emenjivar.luminar.screen.camera.ui.LiveCameraPreview
import com.emenjivar.luminar.screen.camera.ui.MessageHistory
import com.emenjivar.luminar.screen.camera.ui.MessageInputControllers
import com.emenjivar.luminar.screen.camera.ui.rememberLiveCameraController
import com.emenjivar.luminar.screen.settings.SettingsRoute
import com.emenjivar.luminar.ui.components.CustomDialog
import com.emenjivar.luminar.ui.components.CustomDialogAction
import com.emenjivar.luminar.ui.components.MorseText
import com.emenjivar.luminar.ui.components.rememberCustomDialogController
import com.emenjivar.luminar.ui.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.serialization.Serializable
import org.opencv.features2d.SimpleBlobDetector_Params

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    CameraScreenContent(
        uiState = viewModel.state,
        onNavigateToSettings = {
            navController.navigate(SettingsRoute)
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
    uiState: CameraUiState,
    onNavigateToSettings: () -> Unit
) {
    // Compose variables
    val context = LocalContext.current

    // Controllers
    val verticalScroll = rememberScrollState()
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
    val morseCharacter by uiState.morseCharacter.collectAsStateWithLifecycle()
    val messages by uiState.messages.collectAsStateWithLifecycle()
    val debugMorse by uiState.debugMorse.collectAsStateWithLifecycle()
    val timingData by uiState.timingData.collectAsStateWithLifecycle()
    val circularityRange by uiState.circularityRange.collectAsStateWithLifecycle()
    val blobAreaRange by uiState.blobAreaRange.collectAsStateWithLifecycle()

    val emissionProgress by uiState.emissionProgress.collectAsStateWithLifecycle()
    val isEmissionInProgress by remember {
        derivedStateOf { emissionProgress.isInProgress() }
    }

    // Remembered values
    val verticalJumpPx = with(LocalDensity.current) { verticalJump.toPx() }
    val isDebugEnabled = remember { mutableStateOf(false) }

    var imageWithFilter by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val isFlashTurnOn = remember { mutableStateOf(false) }
    val cameraController = rememberLiveCameraController()
    val hasFlashTorchAvailable by cameraController.hasFlashTorchAvailable.collectAsStateWithLifecycle()

    val previewView = remember {
        PreviewView(context).apply {
            this.scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // TODO: this remember will restart the camera at least 2 times.
    val imageAnalysis = remember(circularityRange, blobAreaRange) {
        CustomImageAnalyzer(
            blobParameters = SimpleBlobDetector_Params().apply {
                _filterByCircularity = true
                _minCircularity = circularityRange.lower
                _maxCircularity = circularityRange.upper

                // Play with these values to discard false positive flashlights
                _filterByArea = true
                _minArea = blobAreaRange.lower
                _maxArea = blobAreaRange.upper
            },
            onDrawImage = { isTurnOn, bitmap ->
                isFlashTurnOn.value = isTurnOn
                imageWithFilter = bitmap
            }
        )
    }

    // SideEffects
    LaunchedEffect(isFlashTurnOn) {
        snapshotFlow { isFlashTurnOn.value }
            .distinctUntilChanged()
            .onEach { isTurnOn ->
                uiState.addFlashState(isTurnOn)
            }.launchIn(this)
    }

    LaunchedEffect(messages) {
        // Scroll to the bottom after receiving a new message
        verticalScroll.animateScrollBy(verticalJumpPx * messages.size)
    }

    LaunchedEffect(morseCharacter) {
        when (morseCharacter) {
            MorseCharacter.DIT, MorseCharacter.DAH -> {
                delay(timingData.spaceLetter)
                uiState.finishLetter()
            }

            MorseCharacter.LETTER_SPACE -> {
                delay(timingData.spaceWord - timingData.spaceLetter)
                uiState.finishWord()
            }

            MorseCharacter.WORD_SPACE -> {
                delay(timingData.endMessage - timingData.spaceWord - timingData.spaceLetter)
                // Like a ouija board
                uiState.finishMessage()
            }

            else -> {}
        }
    }

    LaunchedEffect(hasFlashTorchAvailable) {
        if (hasFlashTorchAvailable == false) {
            Toast.makeText(
                context,
                context.getString(R.string.error_torch_unavailable),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var emissionJob by remember { mutableStateOf<Job?>(null) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)

                // Important to cancel in-progress message emissions when move
                // to another screen or close the application
                emissionJob?.cancel()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        contentColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(
                                alpha = ALPHA_SETTINGS_BUTTON
                            )
                        ),
                        onClick = { onNavigateToSettings() }
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape),
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = stringResource(id = R.string.content_description_settings),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DualCameraPreview(
                isDebugEnabled = isDebugEnabled.value,
                rawPreview = { modifier ->
                    LiveCameraPreview(
                        modifier = modifier,
                        previewView = previewView,
                        analyzer = imageAnalysis,
                        permissionState = permissionState,
                        controller = cameraController
                    )
                },
                debugPreview = { modifier ->
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
                }
            )


            // Controls and message history
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        top = 8.dp,
                        bottom = paddingValues.calculateBottomPadding()
                    )
                    .padding(horizontal = 8.dp)
                    .align(Alignment.BottomCenter)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                MessageHistory(
                    messages = messages,
                    isSendingInProgress = isEmissionInProgress,
                    verticalScroll = verticalScroll
                )
                MessageInputControllers(
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = hasFlashTorchAvailable ?: false && !isEmissionInProgress,
                    progress = { emissionProgress },
                    onClickSend = { message ->
                        emissionJob = coroutineScope.launch {
                            uiState.emission.collect { isTorchOn ->
                                yield() // Make the coroutine cancellable
                                if (isTorchOn) {
                                    cameraController.turnOnTorch()
                                } else {
                                    cameraController.turnOffTorch()
                                }
                            }
                        }
                        emissionJob?.start()
                        uiState.onTranslateToMorse(message)
                    },
                    onStopEmission = {
                        emissionJob?.cancel()
                        cameraController.turnOffTorch()
                    }
                )
            }

            // Morse indicator
            if (debugMorse.isNotBlank()) {
                MorseText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .imePadding(),
                    text = debugMorse
                )
            }
        }
    }

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

private const val ALPHA_SETTINGS_BUTTON = 0.2f

// This preview fails due to the permissionRequest
@Preview
@Composable
private fun CameraScreenPreview() {
    AppTheme {
        CameraScreenContent(
            uiState = CameraUiState(
                morseCharacter = MutableStateFlow(MorseCharacter.DIT),
                lastDuration = MutableStateFlow(0L),
                messages = MutableStateFlow(listOf("hello", "how are you", "good bye")),
                debugMorse = MutableStateFlow("-"),
                timingData = MutableStateFlow(TimingData(dit = 0L)),
                circularityRange = MutableStateFlow(Range(0f, 1f)),
                blobAreaRange = MutableStateFlow(Range(0f, 1f)),
                lightBPM = MutableStateFlow(60),
                emission = emptyFlow(),
                isLoading = MutableStateFlow(false),
                emissionProgress = MutableStateFlow(0f),
                addFlashState = {},
                finishLetter = {},
                finishWord = {},
                finishMessage = {},
                clearText = {},
                onTranslateToMorse = {},
                onReset = {}
            ),
            onNavigateToSettings = {}
        )
    }
}

@Serializable
object HomeRoute

private val verticalJump = 20.dp
