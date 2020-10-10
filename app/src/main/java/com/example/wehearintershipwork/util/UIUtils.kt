package com.example.wehearintershipwork.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun toVisibility(constraint: Boolean): Int = if (constraint) {
    View.VISIBLE
} else {
    View.GONE
}

/** display the soft keyboard if given activity is focused */
fun Activity.showKeyboard() {
    currentFocus?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(v, 0)
    }
}

/** hide the soft keyboard if given activity is not focused */
fun Activity.hideKeyboard() {
    currentFocus?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}
