package com.emenjivar.luminar.screen.camera

import android.Manifest
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ext.settingsIntent
import com.emenjivar.luminar.ui.components.CustomDialog
import com.emenjivar.luminar.ui.components.CustomDialogAction
import com.emenjivar.luminar.ui.components.MorseText
import com.emenjivar.luminar.ui.components.SwitchButton
import com.emenjivar.luminar.ui.components.rememberCustomDialogController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel()
) {
    CameraScreenContent(uiState = viewModel.state)
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraScreenContent(
    uiState: CameraUiState
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
    val morseCharacter by uiState.morseCharacter.collectAsState()
    val messages by uiState.messages.collectAsState()
    val debugMorse by uiState.debugMorse.collectAsState()
    val circularityRange by uiState.circularityRange.collectAsState()
    val blobRadiusRange by uiState.blobRadiusRange.collectAsState()
    val lightBPM by uiState.lightBPM.collectAsState()

    // Remembered values
    val verticalJumpPx = with(LocalDensity.current) { verticalJump.toPx() }
    val isDebugEnabled = remember { mutableStateOf(false) }
    val shouldDisplaySettings = remember { mutableStateOf(false) }

    var imageWithFilter by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val isFlashTurnOn = remember { mutableStateOf(false) }

    val previewView = remember {
        PreviewView(context).apply {
            this.scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val imageAnalysis = remember {
        CustomImageAnalyzer(
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

    // TODO: this block could be moved to the viewModel
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

            MorseCharacter.WORD_SPACE -> {
                delay(CameraViewModel.END_MESSAGE - CameraViewModel.SPACE_WORD - CameraViewModel.SPACE_LETTER)
                // Like a ouija board
                uiState.finishMessage()
            }

            else -> {}
        }
    }

    Scaffold(
        contentColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(8.dp)
                            .clickable {
                                shouldDisplaySettings.value = !shouldDisplaySettings.value
                            },
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape),
                            painter = painterResource(
                                id = if (shouldDisplaySettings.value) {
                                    R.drawable.ic_close
                                } else {
                                    R.drawable.ic_settings
                                }
                            ),
                            contentDescription = "Settings",
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
                .padding(paddingValues.calculateBottomPadding())
        ) {
            DualCameraPreview(
                isDebugEnabled = isDebugEnabled.value,
                rawPreview = { modifier ->
                    LiveCameraPreview(
                        modifier = modifier,
                        previewView = previewView,
                        analyzer = imageAnalysis,
                        permissionState = permissionState
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

            if (shouldDisplaySettings.value) {
                CameraSettings(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    circularityRange = { circularityRange },
                    blobRadiusRange = { blobRadiusRange },
                    lightBPM = { lightBPM },
                    onSetCircularity = uiState.onSetCircularity,
                    onSetBlobRadius = uiState.onSetBlobRadius,
                    onSetLightBPM = uiState.onSetLightBPM
                )
            } else {
                MessageHistory(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    messages = messages,
                    verticalScroll = verticalScroll
                )
                SwitchButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    text = "Debug",
                    isEnabled = isDebugEnabled.value,
                    onEnable = { isEnabled ->
                        isDebugEnabled.value = isEnabled
                    }
                )
            }

            if (debugMorse.isNotBlank()) {
                MorseText(
                    modifier = Modifier.align(Alignment.Center),
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

private val verticalJump = 20.dp
