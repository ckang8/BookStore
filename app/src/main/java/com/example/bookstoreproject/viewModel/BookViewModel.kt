package com.example.bookstoreproject.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

data class Book(
    val id: String = "",
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val bookDescription: String = "",
    val bookImage: String? = null,
    val createdDate: String = ""
) {
    fun getBookImageUri(): Uri? {
        return bookImage?.let { Uri.parse(it) }
    }
}

class BookViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var books: SnapshotStateList<Book> = mutableStateListOf()
        private set

    var selectedBook = mutableStateOf<Book?>(null)
        private set

    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        db.collection("books")
            .orderBy("createdDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                books.clear()
                for (document in result) {
                    val book = document.toObject(Book::class.java)
                    books.add(book.copy(id = document.id))
                }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }


    fun fetchBookWithID(bookId: String) {
        db.collection("books").document(bookId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val book = document.toObject(Book::class.java)
                    selectedBook.value = book?.copy(id = document.id)
                } else {
                    // Handle case where the document does not exist
                    selectedBook.value = null
                }
            }
            .addOnFailureListener { e ->
                // Handle error
                selectedBook.value = null
            }
    }

    fun deleteBookWithID(bookId: String) {
        db.collection("books").document(bookId)
            .delete()
            .addOnSuccessListener {
                // Remove the book from the local list
                books.removeAll { it.id == bookId }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    fun updateBookWithID(bookId: String, updatedBook: Book) {
        db.collection("books").document(bookId)
            .set(updatedBook)
            .addOnSuccessListener {
                // Update the local list if necessary
                val index = books.indexOfFirst { it.id == bookId }
                if (index != -1) {
                    books[index] = updatedBook.copy(id = bookId)
                }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    fun createBook(title: String, author: String, description: String, imageUri: Uri) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(System.currentTimeMillis())

        val bookData = hashMapOf(
            "bookTitle" to title,
            "bookAuthor" to author,
            "bookDescription" to description,
            "createdDate" to currentDate,
            "bookImage" to imageUri.toString()
        )

        db.collection("books")
            .add(bookData)
            .addOnSuccessListener { documentReference ->
                val newBook = Book(
                    id = documentReference.id,
                    bookTitle = title,
                    bookAuthor = author,
                    bookDescription = description,
                    bookImage = imageUri.toString(),
                    createdDate = currentDate
                )
                books.add(newBook)
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

}