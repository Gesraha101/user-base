package com.spark.userbase.feature.add_user.di

import com.spark.userbase.feature.add_user.data.repo.UserRepositoryImpl
import com.spark.userbase.feature.add_user.data.repo.source.AddUserLocalDataSource
import com.spark.userbase.feature.add_user.data.source.local.AddUserLocalDataSourceImpl
import com.spark.userbase.feature.add_user.domain.repo.UserRepository
import com.spark.userbase.feature.add_user.presentation.ui.stateholder.AddUserStateHolderImpl
import com.spark.userbase.feature.add_user.presentation.viewmodel.stateholder.AddUserStateHolder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AddUserModule {

    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @ViewModelScoped
    abstract fun bindAddUserLocalDataSource(impl: AddUserLocalDataSourceImpl): AddUserLocalDataSource

    @Binds
    @ViewModelScoped
    abstract fun bindAddUserStateHolder(impl: AddUserStateHolderImpl): AddUserStateHolder
}
