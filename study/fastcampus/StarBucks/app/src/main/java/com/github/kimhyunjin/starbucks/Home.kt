package com.github.kimhyunjin.starbucks

data class Home(
    val user: User,
    val appbarImage: String
)

data class User(
    val nickname: String,
    val starCount: Int,
    val totalCount: Int
)