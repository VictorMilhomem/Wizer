package com.github.wizerapp.utils

import android.net.Uri

fun generateCustomQuizLink(quizId: String): Uri {
    return Uri.parse("wizerapp://quiz?id=$quizId")
}



