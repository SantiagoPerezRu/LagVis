package com.example.lagvis_v1.dominio.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error   -> this
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error   -> null
    }

    fun exceptionOrNull(): Throwable? = when (this) {
        is Success -> null
        is Error   -> cause
    }

    companion object {
        fun <T> ok(value: T): Result<T> = Success(value)
        fun <T> fail(message: String, cause: Throwable? = null): Result<T> =
            Error(message, cause)
    }
}
