package com.emenjivar.luminar.di

import com.emenjivar.luminar.translator.TranslatorRepository
import com.emenjivar.luminar.translator.TranslatorRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTranslatorRepository(): TranslatorRepository =
        TranslatorRepositoryImp(initTree = true)
}
