package com.spark.userbase.feature.add_user.data.repo

import com.spark.userbase.feature.add_user.data.model.toDomain
import com.spark.userbase.feature.add_user.data.model.toEntity
import com.spark.userbase.feature.add_user.data.repo.source.AddUserLocalDataSource
import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.domain.repo.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: AddUserLocalDataSource,
) : UserRepository {

    override suspend fun addUser(user: User): Result<Unit> = runCatching {
        localDataSource.insertUser(user.toEntity())
    }

    override fun getUsers(): Flow<Result<List<User>>> = localDataSource.getAllUsers()
        .map { entities -> Result.success(entities.map { it.toDomain() }) }
        .catch { emit(Result.failure(it)) }
}
