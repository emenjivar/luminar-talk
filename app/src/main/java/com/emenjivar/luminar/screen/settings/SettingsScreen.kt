package com.emenjivar.luminar.screen.settings

import android.util.Range
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.emenjivar.luminar.R
import com.emenjivar.luminar.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
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
                value = "${circularityRange.lower} to ${circularityRange.upper}",
                description = stringResource(id = R.string.settings_circularity_help),
                onClick = {}
            )
            SettingItem(
                title = stringResource(id = R.string.settings_radius),
                value = "${blobRadiusRange.lower}px to ${blobRadiusRange.upper}px",
                description = stringResource(id = R.string.settings_radius_help),
                onClick = {}
            )
            SettingItem(
                title = stringResource(id = R.string.settings_bpm),
                value = "$lightBPM bpm",
                description = stringResource(id = R.string.settings_bpm_help),
                onClick = {}
            )
        }
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
                onReset = {}
            ),
            onNavigateBack = {}
        )
    }
}
