package com.spark.userbase.feature.add_user.domain.repo

import com.spark.userbase.feature.add_user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>
    fun getUsers(): Flow<Result<List<User>>>
}
