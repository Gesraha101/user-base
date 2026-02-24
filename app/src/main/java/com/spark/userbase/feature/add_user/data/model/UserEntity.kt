package com.spark.userbase.feature.add_user.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spark.userbase.feature.add_user.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val jobTitle: String,
    val gender: String,
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    age = age,
    jobTitle = jobTitle,
    gender = gender,
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    age = age,
    jobTitle = jobTitle,
    gender = gender,
)
