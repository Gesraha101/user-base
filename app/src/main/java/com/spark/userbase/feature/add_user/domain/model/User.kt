package com.spark.userbase.feature.add_user.domain.model

data class User(
    val id: Long = 0,
    val name: String,
    val age: Int,
    val jobTitle: String,
    val gender: String,
)
