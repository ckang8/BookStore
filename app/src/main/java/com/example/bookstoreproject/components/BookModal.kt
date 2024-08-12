package com.example.bookstoreproject.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.bookstoreproject.R
import com.example.bookstoreproject.RequestPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun BookModal (
    bookTitle: String,
    onBookTitleChange: (String) -> Unit,
    bookAuthor: String,
    onBookAuthorChange: (String) -> Unit,
    bookDescription: String,
    onBookDescriptionChange: (String) -> Unit,
    bookImage: Uri?, // Change from String to Uri
    onBookImageChange: (Uri?) -> Unit,
    containerColor: Color? = null,
    isView: Boolean,
    isEdit: Boolean,
//    isReset: Boolean
) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false)}

    // Check for permissions and handle them
    RequestPermissions {
        permissionGranted = true
    }
    //image
    var selectedImageUri by remember { mutableStateOf<Uri?>(bookImage) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            onBookImageChange(uri)
        }
    )

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri?.let { uri ->
                onBookImageChange(uri)
            }
        } else {
            Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
        }
    }


    val containerColorSelect = containerColor ?: Color(0xFFEBEBE0)

    fun createImageUri(context: Context): Uri? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File = context.getExternalFilesDir(null) ?: context.filesDir

            // Ensure the directory exists
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val imageFile = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            uri
        } catch (e: Exception) {
            null
        }
    }

    val handleOptionSelected: (String) -> Unit = { option ->
        when (option) {
            "Dismiss" -> {
                showBottomSheet = false
            }
            "Photo Library" -> {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
                showBottomSheet = false
            }
            "Camera" -> {
                imageUri = createImageUri(context)
                imageUri?.let { uri ->
                    takePictureLauncher.launch(uri)
                }
                showBottomSheet = false
            }
        }
    }



    Column(
 //       modifier = Modifier.verticalScroll(rememberScrollState()) // Add scrolling capability
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 55.dp)
                .height(500.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp, // Border width
                    color = Color.Gray, // Border color
                    shape = RoundedCornerShape(16.dp) // Apply the same radius to the border
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = selectedImageUri ?: bookImage,
                    contentDescription = null,
                    modifier = Modifier
                        .background(Color.Black)
                        .height(150.dp) // Set height
                        .clickable {
                            if (!isView || isEdit)
                                showBottomSheet = true
                        },
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(16.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(containerColorSelect,RoundedCornerShape(16.dp))
                        .padding(8.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Name : ")
                    //Book Title
                    TextField(
                        value = bookTitle,
                        onValueChange = { newTitle -> onBookTitleChange(newTitle) },
                        maxLines = 1,
                        enabled = if (!isView || isEdit) true else false,
                        colors = CustomTextFieldColors(),

                    )
                }

                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(16.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(containerColorSelect,RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Author : ")
                    //Book Author
                    TextField(
                        value = bookAuthor,
                        onValueChange = { newAuthor -> onBookAuthorChange(newAuthor) },
                        maxLines = 1,
                        enabled = if (!isView || isEdit) true else false,
                        colors = CustomTextFieldColors()
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .border(
                            width = 2.dp, // Border width
                            color = Color.Gray, // Border color
                            shape = RoundedCornerShape(16.dp) // Apply the same radius to the border
                        )
                        .background(containerColorSelect,RoundedCornerShape(16.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = bookDescription,
                        onValueChange = { newDescription -> onBookDescriptionChange(newDescription) },
                        minLines = 5,
                        maxLines = 5,
                        enabled = if (!isView || isEdit) true else false,
                        label = { Text("Description...") },
                        colors = CustomTextFieldColors()
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        BottomSheet(
            onClickSheet = true,
            optionSelected= handleOptionSelected
        )
    }
}

@Composable
fun CustomTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        disabledTextColor = Color.Black,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
}