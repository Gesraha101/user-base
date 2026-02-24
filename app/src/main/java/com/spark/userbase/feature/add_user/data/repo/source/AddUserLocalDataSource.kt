package com.spark.userbase.feature.add_user.data.repo.source

import com.spark.userbase.feature.add_user.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface AddUserLocalDataSource {
    suspend fun insertUser(user: UserEntity)
    fun getAllUsers(): Flow<List<UserEntity>>
}
