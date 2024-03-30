package com.emenjivar.luminar.data

import android.content.Context
import android.util.Range
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingPreferencesImp @Inject constructor(
    private val context: Context
) : SettingPreferences {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val minCircularity = floatPreferencesKey("minCircularity")
    private val maxCircularity = floatPreferencesKey("maxCircularity")
    private val minRadius = floatPreferencesKey("minRadius")
    private val maxRadius = floatPreferencesKey("maxRadius")

    override fun getCircularity() = context.dataStore.data
        .map { settings ->
            val min = settings[minCircularity] ?: 0f
            val max = settings[maxCircularity] ?: 1f
            Range(min, max)
        }

    override suspend fun setCircularity(range: Range<Float>) {
        context.dataStore.edit { settings ->
            settings[minCircularity] = range.lower
            settings[maxCircularity] = range.upper
        }
    }

    override fun getBlobRadius() = context.dataStore.data
        .map { settings ->
            val min = settings[minRadius] ?: 0f
            val max = settings[maxRadius] ?: 0f
            Range(min, max)
        }

    override suspend fun setBlobRadius(range: Range<Float>) {
        context.dataStore.edit { settings ->
            settings[minRadius] = range.lower
            settings[maxRadius] = range.upper
        }
    }
}

interface SettingPreferences {
    /**
     * Gets the range of the circularity for the blobs of light.
     * 0f is a shape with any form and 1f is a perfect circle
     */
    fun getCircularity(): Flow<Range<Float>>

    /**
     * Set the range of circularity for the blobs of light.
     * 0f is a shape with any form and 1f is a perfect circle
     */
    suspend fun setCircularity(range: Range<Float>)

    /**
     * Get the minimum and maximum radius for the light blobs,
     * the flow emits pixel values
     */
    fun getBlobRadius(): Flow<Range<Float>>

    /**
     * Set the range of radius for the light blobs.
     * The values must be in pixels
     */
    suspend fun setBlobRadius(range: Range<Float>)
}
