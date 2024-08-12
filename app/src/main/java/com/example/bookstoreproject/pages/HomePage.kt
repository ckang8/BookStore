package com.example.bookstoreproject.pages

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookstoreproject.components.BookListItem
import com.example.bookstoreproject.components.Header
import com.example.bookstoreproject.components.NothingPage
import com.example.bookstoreproject.viewModel.BookViewModel


@Composable
fun HomePage(
    navController: NavController,
) {
    val viewModel: BookViewModel = viewModel()

    var addBookClick by remember { mutableStateOf(false) }
    val books = viewModel.books
    var selectedBookId by remember { mutableStateOf<String?>(null) } // Track selected book
    var dialogType by remember { mutableStateOf<DialogType?>(null) }
    val context = LocalContext.current


    BackHandler(enabled = true) {}

    Column {
        Header(
            labelLeft = "Logout",
            labelRight = "",
            isAdd = true,

            isLogout = true,
            onLogout = {
                dialogType = DialogType.Logout
            },
            onAddbook = {
                addBookClick = true
            },
        )


        if(books.isNotEmpty()) {
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = 55.dp
                    ) // Padding on all sides, with extra bottom padding
                    .background(
                        color = Color.White,
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(16.dp)
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                items(books) { book ->
                    if (book != null) {
                        BookListItem(
                            book,
                            isSelected = book.id == selectedBookId,
                            onClick = {
                                Log.d("HomePageBook","${selectedBookId}")
                                selectedBookId = if (selectedBookId == book.id) null else book.id // Toggle selection
                                navController.navigate("viewEdit/${selectedBookId}")
                            },
                            onDelete = {
                                selectedBookId = book.id
                                dialogType = DialogType.Delete
                            }
                        )
                    } else {
                        Text(text = "Data Not Found.")
                    }
                }
            }
        } else {
            NothingPage()
        }

    }

    if(addBookClick) {
        Log.d("HomePage","addbook click")
        navController.navigate("create")
        selectedBookId = null
    }

    if (dialogType != null) {
        val (title, text, confirmAction) = when (dialogType) {
            DialogType.Logout -> Triple(
                "Confirm Logout",
                "Are you sure you want to logout?",
                {
                    navController.navigate("login")
                    selectedBookId = null
                }
            )
            DialogType.Delete -> Triple(
                "Confirmation",
                "Are you sure you want to delete?",
                {
                    selectedBookId?.let { id ->
                        viewModel.deleteBookWithID(id)
                    }
                }
            )
            null -> Triple("", "", {})
        }

        AlertDialog(
            onDismissRequest = {
                dialogType = null // Reset dialog type to dismiss
                selectedBookId = null
            },
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(
                    onClick = {
                        confirmAction()
                        dialogType = null // Reset dialog type after confirmation
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dialogType = null // Reset dialog type to dismiss
                        selectedBookId = null
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

enum class DialogType {
    Logout,
    Delete
}

