package com.example.bookstoreproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookstoreproject.pages.CreateBook
import com.example.bookstoreproject.pages.HomePage
import com.example.bookstoreproject.pages.LoginScreen
import com.example.bookstoreproject.pages.ViewEditBook
import com.example.bookstoreproject.ui.theme.BookStoreProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookStoreProjectTheme {
                NavigationRoutes()
            }
        }
    }
}

@Composable
fun NavigationRoutes() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomePage(navController) }
        composable("create") { CreateBook(navController) }
        composable("viewEdit/{bookId}") { navigate ->
            val bookId = navigate.arguments?.getString("bookId")
            ViewEditBook(navController, bookId)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BookStoreProjectTheme {
        NavigationRoutes()
    }
}

@Preview
@Composable
fun testCase() {
    Text(text = "123")
}





