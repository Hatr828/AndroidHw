package com.example.androidhw

data class UserModel(
    val avatarResId: Int,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val country: String,
    val city: String
) {
    fun fullName(): String = "$firstName $lastName"
}
