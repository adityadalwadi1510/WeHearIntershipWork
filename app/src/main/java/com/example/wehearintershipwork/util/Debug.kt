package com.example.wehearintershipwork.util

import android.util.Log
import com.example.wehearintershipwork.util.LogTags

fun printAuthDebugLog(logMsg: String) {
    Log.d(LogTags.AUTH_DEBUG, logMsg)
}

fun printAuthRepoLog(logMsg: String) {
    Log.d(LogTags.AUTH_REPO, logMsg)
}

fun printAuthViewModelLog(logMsg: String) {
    Log.d(LogTags.AUTH_VIEW_MODEL, logMsg)
}

fun printAuthActivityLog(logMsg: String) {
    Log.d(LogTags.AUTH_ACTIVITY, logMsg)
}

fun printAuthFragmentLog(logMsg: String) {
    Log.d(LogTags.AUTH_FRAGMENT, logMsg)
}