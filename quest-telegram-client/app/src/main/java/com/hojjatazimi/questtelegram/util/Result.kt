package com.hojjatazimi.questtelegram.util

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val message: String, val cause: Throwable? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
