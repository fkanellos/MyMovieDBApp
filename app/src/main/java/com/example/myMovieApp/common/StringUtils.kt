package com.example.myMovieApp.common

class StringUtils {
    companion object {
        fun isEmptyOrNull(text: String?): Boolean {
            if (text != null && text.length > 1) {
                return true
            }
            return false
        }
    }
}