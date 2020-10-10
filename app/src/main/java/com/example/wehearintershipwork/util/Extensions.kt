package com.example.wehearintershipwork.util

import android.text.Editable

/**
 * convert the string to editable for the editText
 */
fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)