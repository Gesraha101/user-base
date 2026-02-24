package com.spark.userbase.feature.add_user.domain.usecase

import com.spark.userbase.feature.add_user.domain.model.User
import com.spark.userbase.feature.add_user.domain.repo.UserRepository
import javax.inject.Inject

class AddUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(user: User): Result<Unit> = repository.addUser(user)
}
