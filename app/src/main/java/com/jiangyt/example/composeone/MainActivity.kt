package com.jiangyt.example.composeone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jiangyt.example.composeone.ui.ComposeViewActivity
import com.jiangyt.example.composeone.ui.theme.ComposeOneTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeOneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OpenPage("打开Compose页面") {
                        startActivity(Intent(this, ComposeViewActivity::class.java))
                    }
                }
            }
        }
    }

}

@Composable
fun OpenPage(title:String, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Text(text = title)
    }
}

@Preview
@Composable
fun PreviewOpenPage() {
    OpenPage("打开Compose页面") {

    }
}
