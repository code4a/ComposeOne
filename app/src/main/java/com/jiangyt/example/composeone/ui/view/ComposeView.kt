package com.jiangyt.example.composeone.ui.view

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import com.jiangyt.example.composeone.ui.Message

@Composable
fun ChatBubble(message: Message) {
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }
    ClickableText(text = AnnotatedString(message.body), onClick = { showDetails = !showDetails })
    if (showDetails) {
        Text(text = message.author)
    }
}