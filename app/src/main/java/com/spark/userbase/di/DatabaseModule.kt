package com.spark.userbase.di

import android.content.Context
import androidx.room.Room
import com.spark.userbase.feature.add_user.data.source.local.room.UserDao
import com.spark.userbase.feature.add_user.data.source.local.room.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase =
        Room.databaseBuilder(context, UserDatabase::class.java, "user_database").build()

    @Provides
    @Singleton
    fun provideUserDao(database: UserDatabase): UserDao = database.userDao()
}
