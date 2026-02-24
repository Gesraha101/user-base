package com.spark.userbase.feature.add_user.domain.usecase

import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.domain.repo.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<Result<List<User>>> = repository.getUsers()
}
