package com.blueduck.easydentist.util


// to handle the api response
// Response that can represent three possible outcomes of an operation: Loading, Success, or Failure.
sealed class Response<out T> {
    object Loading: Response<Nothing>()

    data class Success<out T>(val data: T?): Response<T>()

    data class Failure(val e: Exception): Response<Nothing>()
}