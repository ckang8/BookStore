package com.example.bookstoreproject.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookstoreproject.components.CustomTextField
import com.example.bookstoreproject.components.Header

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val textStyle = TextStyle(
        fontSize = 20.sp
    )
    BackHandler(enabled = true) {}

    Header(
        labelLeft = "",
        labelRight = "",
        isAdd = false,
        isLogin = true
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
//        OutlinedTextField(
//            value = name,
//            onValueChange = { text ->
//                name = text
//            },
//            modifier = Modifier
//                .padding(bottom = 16.dp),
//            label = { Text("Username", style = textStyle)},
//            textStyle = textStyle
//        )
        //Username
        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            isPassword = false,
            textStyle = textStyle
        )
        //Password
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            textStyle = textStyle
        )
        //Login Btn
        Button(onClick = {
            if (username == "SS" && password == "11111") {
                navController.navigate("home") {
//                    popUpTo("login") { inclusive = true }
                }
            } else {

            }
        }) {
            Text(text = "Login", style = textStyle)
        }
    }
}