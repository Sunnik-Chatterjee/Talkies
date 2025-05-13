package com.example.talkies.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talkies.R

@Composable
@Preview(showSystemUi = true)
fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.talkies), contentDescription = null,
            modifier = Modifier.size(80.dp).align(Alignment.Center))
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Text(text = "Welcome to Talkies", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        }
    }
}