package com.emenjivar.luminar.screen.settings

import android.util.Range
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.util.toRange
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ext.twoDecimals
import com.emenjivar.luminar.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    SettingsContent(
        uiState = viewModel.uiState,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onNavigateBack: () -> Unit
) {
    val circularityRange by uiState.circularityRange.collectAsStateWithLifecycle()
    val blobRadiusRange by uiState.blobRadiusRange.collectAsStateWithLifecycle()
    val lightBPM by uiState.lightBPM.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val settingsItem = remember { mutableStateOf(SettingsItem.default()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SettingItem(
                title = stringResource(id = R.string.settings_circularity),
                value = "${circularityRange.lower.twoDecimals()} " +
                    "to ${circularityRange.upper.twoDecimals()}",
                description = stringResource(id = R.string.settings_circularity_help),
                onClick = {
                    coroutineScope.launch {
                        settingsItem.value = SettingsItem(
                            titleRes = R.string.settings_circularity,
                            range = 0f..1f,
                            initialSelection = SettingsSliderSelection.Range(
                                circularityRange.lower..circularityRange.upper
                            ),
                            onClick = { selection ->
                                if (selection is SettingsSliderSelection.Range) {
                                    uiState.onSetCircularity(selection.range.toRange())
                                }

                            }
                        )
                        bottomSheetState.expand()
                    }
                }
            )
            SettingItem(
                title = stringResource(id = R.string.settings_radius),
                value = "${blobRadiusRange.lower.twoDecimals()}px " +
                    "to ${blobRadiusRange.upper.twoDecimals()}px",
                description = stringResource(id = R.string.settings_radius_help),
                onClick = {
                    coroutineScope.launch {
                        settingsItem.value = SettingsItem(
                            titleRes = R.string.settings_radius,
                            range = 0f..200f,
                            initialSelection = SettingsSliderSelection.Range(
                                blobRadiusRange.lower..blobRadiusRange.upper
                            ),
                            onClick = { selection ->
                                if (selection is SettingsSliderSelection.Range) {
                                    uiState.onSetBlobRadius(selection.range.toRange())
                                }
                            }
                        )
                        bottomSheetState.expand()
                    }
                }
            )
            SettingItem(
                title = stringResource(id = R.string.settings_bpm),
                value = "$lightBPM bpm",
                description = stringResource(id = R.string.settings_bpm_help),
                onClick = {
                    coroutineScope.launch {
                        settingsItem.value = SettingsItem(
                            titleRes = R.string.settings_radius,
                            range = 40f..100f,
                            initialSelection = SettingsSliderSelection.Single(lightBPM),
                            onClick = { selection ->
                                if (selection is SettingsSliderSelection.Single) {
                                    uiState.onSetLightBPM(selection.value)
                                }
                            }
                        )
                        bottomSheetState.expand()
                    }
                }
            )
        }
    }

    if (bottomSheetState.isVisible) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }
        ) {
            SettingsEditionModal(
                title = stringResource(id = settingsItem.value.titleRes),
                min = settingsItem.value.range.start,
                max = settingsItem.value.range.endInclusive,
                initialSelection = settingsItem.value.initialSelection,
                onSaveClick = { selection ->
                    coroutineScope.launch {
                        settingsItem.value.onClick(selection)
                        bottomSheetState.hide()
                    }
                },
                onCancelClick = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                }
            )
        }
    }
}

data class SettingsItem(
    @StringRes val titleRes: Int,
    val range: ClosedFloatingPointRange<Float>,
    val initialSelection: SettingsSliderSelection,
    val onClick: (selection: SettingsSliderSelection) -> Unit
) {
    companion object {
        fun default() = SettingsItem(
            titleRes = R.string.settings_bpm,
            range = 60f..100f,
            initialSelection = SettingsSliderSelection.Single(60),
            onClick = {}
        )
    }
}

@Serializable
object SettingsRoute

@Preview
@Composable
private fun SettingContentPreview() {
    AppTheme {
        SettingsContent(
            uiState = SettingsUiState(
                circularityRange = MutableStateFlow(Range(0f, 1f)),
                blobRadiusRange = MutableStateFlow(Range(20f, 50f)),
                lightBPM = MutableStateFlow(55),
                onSetCircularity = {},
                onSetBlobRadius = {},
                onSetLightBPM = {},
                onReset = {}
            ),
            onNavigateBack = {}
        )
    }
}
