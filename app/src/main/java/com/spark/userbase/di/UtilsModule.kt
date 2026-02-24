package com.spark.userbase.di

import android.content.Context
import com.spark.userbase.common.ui.util.StringUnwrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {
    @Provides
    @Singleton
    fun provideUnwrapper(@ApplicationContext context: Context): StringUnwrapper =
        StringUnwrapper(context)
}