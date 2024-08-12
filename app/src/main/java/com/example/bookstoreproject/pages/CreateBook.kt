package com.example.bookstoreproject.pages

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookstoreproject.R
import com.example.bookstoreproject.components.BookModal
import com.example.bookstoreproject.components.Header
import com.example.bookstoreproject.viewModel.BookViewModel


@Composable
fun CreateBook (navController: NavController) {
    val bookViewModel: BookViewModel = viewModel()

    var onBackClick by remember { mutableStateOf(false) }
    var onCreateClick by remember { mutableStateOf(false) }
    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookDescription by remember { mutableStateOf("") }
    var bookImage by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(enabled = true) {}

    Column {
        Header(
            labelLeft = "Back",
            labelRight = "Done",
            isBook = true,
            onBack = {
                Log.d("HomePage", "Back button clicked")
                onBackClick = true
            },
            onCreate = {
                onCreateClick = true
            },
        )
        BookModal(
            bookTitle = bookTitle,
            onBookTitleChange = { bookTitle = it },
            bookAuthor = bookAuthor,
            onBookAuthorChange = { bookAuthor = it },
            bookDescription = bookDescription,
            onBookDescriptionChange = { bookDescription = it },
            bookImage = bookImage,
            onBookImageChange = { bookImage = it },
            isView = false,
            isEdit = false,
//            isReset = false
        )
    }

    if(onBackClick) {
        navController.popBackStack()
    } else if(onCreateClick) {
        keyboardController?.hide()
        if (bookTitle.isBlank() || bookAuthor.isBlank() || bookDescription.isBlank()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            onCreateClick = false
        } else if (bookTitle.length > 30 || bookAuthor.length > 30 || bookDescription.length > 150) {
            onCreateClick = false
        }  else {
            val bookImageUri = bookImage ?: Uri.parse("android.resource://${context.packageName}/${R.drawable.ic_launcher_foreground}")
//            saveBookToFirestore(bookTitle, bookAuthor, bookDescription, imageUri, navController)
            bookViewModel.createBook(bookTitle, bookAuthor, bookDescription, bookImageUri)
            navController.navigate("home")
            onCreateClick = false
        }
    }
}
