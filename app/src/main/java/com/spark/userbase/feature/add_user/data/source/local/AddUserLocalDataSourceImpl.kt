package com.spark.userbase.feature.add_user.data.source.local

import com.spark.userbase.feature.add_user.data.model.UserEntity
import com.spark.userbase.feature.add_user.data.repo.source.AddUserLocalDataSource
import com.spark.userbase.feature.add_user.data.source.local.room.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddUserLocalDataSourceImpl @Inject constructor(
    private val dao: UserDao,
) : AddUserLocalDataSource {
    override suspend fun insertUser(user: UserEntity) = dao.insertUser(user)
    override fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
}
