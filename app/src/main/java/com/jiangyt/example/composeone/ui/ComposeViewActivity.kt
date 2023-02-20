package com.jiangyt.example.composeone.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jiangyt.example.composeone.R
import com.jiangyt.example.composeone.ui.theme.ComposeOneTheme

class ComposeViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeOneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    //MessageCard(message = Message("jiangyt", "你好啊，这是我给你发的消息"))
                    Conversation(messages = createData())
                }
            }
        }
    }
}


@Composable
fun HelloContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        var name by remember { mutableStateOf("") }
        if (name.isNotEmpty()) {
            Text(
                text = "Hello $name",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name") })
    }
}

@Preview
@Composable
fun PreviewHelloContent() {
    HelloContent()
}

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { msg ->
            MessageCard(message = msg)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConversation() {
    ComposeOneTheme {
        Conversation(messages = createData())
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeOneTheme {
        Greeting("Android")
    }
}

@Composable
fun MessageCard(message: Message) {
    Row(modifier = Modifier.padding(all = Dp(8f))) {
        Image(
            painter = painterResource(id = R.mipmap.icon_auth_avatar),
            contentDescription = "联系人头像",
            modifier = Modifier
                .size(Dp(60f))
                .clip(CircleShape)
                .border(Dp(1.5f), MaterialTheme.colorScheme.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(Dp(8f)))
        var isExpanded by remember {
            mutableStateOf(false)
        }
        val surfaceColor by animateColorAsState(
            targetValue = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = message.author,
                color = Color.Red,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(Dp(4f)))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = Dp(1f),
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(Dp(1f))
            ) {
                Text(
                    text = message.body,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(all = Dp(2f)),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageCard() {
    ComposeOneTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MessageCard(message = Message("jiangyt", "你好啊，这是我给你发的消息"))
        }
    }
}

data class Message(val author: String, val body: String)

fun createData(): List<Message> {
    return mutableListOf<Message>().apply {
        for (i in 1..20) {
            add(
                Message(
                    "name$i",
                    "我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体我是消息体$i"
                )
            )
        }
    }
}