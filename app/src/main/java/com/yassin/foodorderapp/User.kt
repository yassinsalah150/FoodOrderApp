package com.yassin.foodorderapp

/**
 * A user profile stored in Realtime Database at /users/{uid}.
 *
 * Every property has a default value so the class gets a no-argument
 * constructor, which Firebase needs to turn database data back into a User.
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "" // "buyer" or "seller"
)
