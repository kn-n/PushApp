package ru.kn_n.pushapp

import androidx.annotation.Keep

@Keep
class MessageModel(
    val title: String,
    val body: String
)

@Keep
class HaveChanges(
    val have_ch: Boolean
)
