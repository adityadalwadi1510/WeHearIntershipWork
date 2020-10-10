package com.example.wehearintershipwork.util

/**
 * Status of a resource that is provided to the UI.
 *
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<Resource<T>>` or `Flow<Resource<T>> to pass back the latest data to the UI with its fetch status.
 */
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}