package com.example.bookstoreproject.pages

import android.net.Uri
import android.util.Log
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
    var blankError by remember { mutableStateOf("") }
    var errorMessages by remember { mutableStateOf(mapOf<String, String>()) }

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
            errorMessages = errorMessages
        )
    }

    if(onBackClick) {
        navController.popBackStack()
    } else if(onCreateClick) {
        keyboardController?.hide()
        val errors = mutableMapOf<String, String>()

        // Validation
        if (bookTitle.isBlank()) {
            errors["bookTitle"] = "Title cannot be blank"
        }

        if (bookAuthor.isBlank()) {
            errors["bookAuthor"] = "Author cannot be blank"
        }

        if (bookDescription.isBlank()) {
            errors["bookDescription"] = "Description cannot be blank"
        }

        // Update error messages state
        errorMessages = errors
        if (bookTitle.length > 50 || bookAuthor.length > 50 || bookDescription.length > 150) {
        } else if (errors.isEmpty()) {
            bookViewModel.createBook(bookTitle, bookAuthor, bookDescription, bookImage)
            navController.navigate("home")
        }
        onCreateClick = false
    }
}
