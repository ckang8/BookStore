package com.example.bookstoreproject.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookstoreproject.components.BookModal
import com.example.bookstoreproject.components.Header
import com.example.bookstoreproject.components.NothingPage
import com.example.bookstoreproject.viewModel.Book
import com.example.bookstoreproject.viewModel.BookViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ViewEditBook (
    navController: NavController,
    bookId: String?
) {
    val viewModel: BookViewModel = viewModel()
    val book by viewModel.selectedBook

    BackHandler(enabled = true) {}

    // Fetch the book when the screen is first composed
    LaunchedEffect(bookId) {
        bookId?.let {
            viewModel.fetchBookWithID(it)
        }
    }
    // State for tracking initial values
    var onBackClick by remember { mutableStateOf(false) }
    var onEditClick by remember { mutableStateOf(false) }
    var onResetClick by remember { mutableStateOf(false) }
    var labelRight by remember { mutableStateOf("Edit") }
    var labelRightColor by remember { mutableStateOf("Red") }

    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var bookDescription by remember { mutableStateOf("") }
    var bookImage by remember { mutableStateOf<Uri?>(null) }
    var titleDefault by remember { mutableStateOf("") }
    var authorDefault by remember { mutableStateOf("") }
    var descriptionDefault by remember { mutableStateOf("") }
    var imageDefault by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    var mode by remember { mutableStateOf(EditMode.VIEW) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorMessages by remember { mutableStateOf(mapOf<String, String>()) }

    book?.let {
        LaunchedEffect(it) {
            titleDefault = it.bookTitle
            authorDefault = it.bookAuthor
            descriptionDefault = it.bookDescription
            imageDefault = it.bookImage?.let { Uri.parse(it) }
            bookTitle = it.bookTitle
            bookAuthor = it.bookAuthor
            bookDescription = it.bookDescription
            bookImage = it.bookImage?.let { Uri.parse(it) }
        }
        Column {
            Header(
                labelLeft = "Back",
                labelRight = if (mode == EditMode.EDIT) "Done" else "Edit",
                isView = mode == EditMode.VIEW,
                isReset = true,
                onBack = {
                    keyboardController?.hide()
                    if (mode == EditMode.VIEW) {
                        //view mode go back to homepage
                        onBackClick = true
                    } else {
                        //edit mode to view mode, disable to edit.
                        mode = EditMode.VIEW
                        // Reset fields to default values
                        bookTitle = titleDefault
                        bookAuthor = authorDefault
                        bookDescription = descriptionDefault
                        bookImage = imageDefault
                        onEditClick = false
                    }
                },
                onEdit = {
                    mode = EditMode.EDIT
                    onEditClick = true
                    labelRight = "Doone"
                },
                onReset = {
                    onResetClick = true

                },
                labelRightColor = if (mode == EditMode.EDIT) Color.Blue else null,
                headerTitle = bookTitle
            )

                Box(
                ) {
                    BookModal(
                        bookTitle = bookTitle,
                        onBookTitleChange = { bookTitle = it },
                        bookAuthor = bookAuthor,
                        onBookAuthorChange = { bookAuthor = it },
                        bookDescription = bookDescription,
                        onBookDescriptionChange = { bookDescription = it },
                        bookImage = bookImage,
                        onBookImageChange = { bookImage = it },
                        containerColor = if (mode == EditMode.VIEW) Color.Transparent else null,
                        isView = mode == EditMode.VIEW,
                        isEdit = mode == EditMode.EDIT,
                        errorMessages = errorMessages

                    )
                }
            }


        if(onBackClick) {
            navController.navigate("home")
            onBackClick = false
            onResetClick = false
        } else if (onResetClick) {
            keyboardController?.hide()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(System.currentTimeMillis())
            val errors = mutableMapOf<String, String>()
            if (bookTitle.isBlank()) {
                errors["bookTitle"] = "Title cannot be blank"
            }
            if (bookAuthor.isBlank()) {
                errors["bookAuthor"] = "Author cannot be blank"
            }
            if (bookDescription.isBlank()) {
                errors["bookDescription"] = "Description cannot be blank"
            }
            errorMessages = errors

            if (bookTitle == it.bookTitle && bookAuthor == it.bookAuthor && bookDescription == it.bookDescription && bookImage.toString() == it.bookImage ) {
                Toast.makeText(context, "No changes made", Toast.LENGTH_SHORT).show()
            } else if (bookTitle.length > 50 || bookAuthor.length > 50 || bookDescription.length > 150) {
            } else if (errors.isEmpty()) {
                val updatedBook = Book(
                    id = it.id,
                    bookTitle = bookTitle,
                    bookAuthor = bookAuthor,
                    bookDescription = bookDescription,
                    bookImage = bookImage?.toString(),
                    createdDate = currentDate
                )
                viewModel.updateBookWithID(it.id, updatedBook)
                mode = EditMode.VIEW
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            }
            onResetClick = false
        }


    } ?: run {
        Column {
            Header(
                labelLeft = "Back",
                labelRight = "Edit",
                isView = true,
                onBack = {
                    onBackClick = true
                },
                headerTitle = "-"
            )
            NothingPage()
        }
    }


}

enum class EditMode {
    VIEW, EDIT
}
