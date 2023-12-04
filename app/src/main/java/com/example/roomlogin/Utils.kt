package com.example.roomlogin

import android.util.Patterns

fun validEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validPassword(password: String): Boolean{
    return password.length > 6
}