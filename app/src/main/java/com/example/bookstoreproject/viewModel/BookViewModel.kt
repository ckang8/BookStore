package com.example.bookstoreproject.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private val storage = FirebaseStorage.getInstance().reference

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
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let {
                    books.clear()
                    for (document in it) {
                        val book = document.toObject(Book::class.java)
                        books.add(book.copy(id = document.id))
                    }
                }
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

    fun createBook(title: String, author: String, description: String, imageUri: Uri?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(System.currentTimeMillis())

        // Prepare book data
        val bookData = hashMapOf(
            "bookTitle" to title,
            "bookAuthor" to author,
            "bookDescription" to description,
            "createdDate" to currentDate
        )

        if (imageUri != null) {
            val imageRef: StorageReference = storage.child("images/${imageUri.lastPathSegment}")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        bookData["bookImage"] = downloadUrl.toString()
                        saveBookDataToFirestore(bookData)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle Storage error
                    Log.e("CreateBook", "Image upload failed: ${e.message}")
                    saveBookDataToFirestore(bookData)
                }
        } else {
            // No image provided
            saveBookDataToFirestore(bookData)
        }
    }

    private fun saveBookDataToFirestore(bookData: Map<String, Any>) {
        db.collection("books")
            .add(bookData)
            .addOnSuccessListener { documentReference ->
                val newBook = Book(
                    id = documentReference.id,
                    bookTitle = bookData["bookTitle"] as String,
                    bookAuthor = bookData["bookAuthor"] as String,
                    bookDescription = bookData["bookDescription"] as String,
                    bookImage = bookData["bookImage"] as? String,
                    createdDate = bookData["createdDate"] as String
                )
                books.add(newBook)
            }
            .addOnFailureListener { e ->
                // Handle Firestore error
                Log.e("CreateBook", "Firestore error: ${e.message}")
            }
    }


    fun updateBookWithID(bookId: String, updatedBook: Book, newImageUri: Uri?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(System.currentTimeMillis())

        // If a new image URI is provided, upload it to Firebase Storage
        if (newImageUri != null) {
            val imageRef = storage.child("images/${newImageUri.lastPathSegment}")
            imageRef.putFile(newImageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // Update the book data with the new image URL
                        val updatedBookData = updatedBook.copy(
                            bookImage = downloadUrl.toString(),
                            createdDate = currentDate
                        )
                        db.collection("books").document(bookId)
                            .set(updatedBookData)
                            .addOnSuccessListener {
                                // Update the local list if necessary
                                val index = books.indexOfFirst { it.id == bookId }
                                if (index != -1) {
                                    books[index] = updatedBookData.copy(id = bookId)
                                }
                            }
                            .addOnFailureListener { e ->
                                // Handle error
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle Storage error
                }
        } else {
            // No new image provided, update Firestore with the existing book data
            val updatedBookData = updatedBook.copy(createdDate = currentDate)
            db.collection("books").document(bookId)
                .set(updatedBookData)
                .addOnSuccessListener {
                    // Update the local list if necessary
                    val index = books.indexOfFirst { it.id == bookId }
                    if (index != -1) {
                        books[index] = updatedBookData.copy(id = bookId)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }


}