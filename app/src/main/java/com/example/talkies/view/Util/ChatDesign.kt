package com.example.talkies.view.Util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.talkies.R
import com.example.talkies.data.model.ChatList
import com.example.talkies.vm.HomeViewModel

@Composable
fun ChatDesign(
    chatList: ChatList,
    onClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val profileImage = chatList?.profileImage
    val bitmap = remember {
        profileImage.let { homeViewModel.base64ToBitmap(it.toString()) }
    }
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = if (bitmap != null) {
                rememberImagePainter(bitmap)
            } else {
                painterResource(R.drawable.defaultprofileimage)
            },
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .background(color = Color.Gray)
                .clip(CircleShape), contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(chatList.name ?: "Unknown", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(chatList.time ?: "--:--", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(chatList.message ?: "", color = Color.Gray, fontSize = 14.sp)
        }
    }
}