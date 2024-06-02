package com.emenjivar.luminar.di

import android.content.Context
import com.emenjivar.luminar.data.SettingPreferences
import com.emenjivar.luminar.data.SettingPreferencesImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideSettingsPreferences(
        @ApplicationContext context: Context
    ): SettingPreferences = SettingPreferencesImp(context = context)
}
