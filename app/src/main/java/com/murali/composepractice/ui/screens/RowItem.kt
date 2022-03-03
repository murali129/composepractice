package com.murali.composepractice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.murali.composepractice.R
import com.murali.composepractice.ui.theme.ComposepracticeTheme

@Composable
fun rowItem(title: String, body: String, navController: NavController?) {

    Row(modifier = Modifier
        .padding(all = 5.dp)
        .clickable {
            navController?.navigate("details/?header="+title)
        }) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, maxLines = 2)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = body)
        }
    }
}

@Preview
@Composable
fun ListRow() {
    ComposepracticeTheme {
        rowItem("This is the header", "This is the body", null)
    }
}

