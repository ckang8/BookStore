package com.example.bookstoreproject.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bookstoreproject.R
import com.example.bookstoreproject.viewModel.Book
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun BookListItem(
    book: Book,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val titleStyle = TextStyle(
        fontSize = 32.sp,
        color = Color.Red,
        fontWeight = FontWeight.Bold
    )
    val normalStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )

    // Default image resource
    val defaultImage: Painter = painterResource(id = R.drawable.ic_launcher_foreground)

    val bookImagePainter: Painter = book.getBookImageUri()?.let { uri ->
        // Load the image from Uri
        rememberAsyncImagePainter(model = uri)
    } ?: defaultImage

    // Format date to show only the date part
    val outputDateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())

    val formattedDate = try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(book.createdDate)
        date?.let { outputDateFormat.format(it) } ?: book.createdDate // Fallback to original if parsing fails
    } catch (e: Exception) {
        book.createdDate // Fallback to original if parsing fails
    }

    val backgroundColor = if (isSelected) Color.LightGray else Color.White
    val shadowElevation = if (isSelected) 8.dp else 0.dp

    val archive = SwipeAction(
        onSwipe = {
            Log.d("BookItem","Archieve")
            onDelete()
        },
        icon ={
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .size(50.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White,

            )
        },
        background = Red,
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .background(color = backgroundColor)
            .shadow(elevation = shadowElevation, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),

    ) {
        SwipeableActionsBox(
            endActions = listOf(archive),
            swipeThreshold = 80.dp
        ){
            Row(modifier = Modifier
                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = bookImagePainter,
                    contentDescription = null,
                    modifier = Modifier
                        .background(Color.Black)
                        .size(80.dp)

                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(25.dp)
                ){
                    Text(
                        text = book.bookTitle,
                        style = titleStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis, // Ensures text is truncated with ellipsis
                        modifier = Modifier
                            .fillMaxWidth() // Make sure it uses available width
                            .widthIn(max = 200.dp) // Set maximum width
                    )
                    Text(
                        text = "By ${book.bookAuthor}",
                        style = normalStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth() // Make sure it uses available width
                            .widthIn(max = 200.dp) // Set maximum width
                    )
                }
                Text(text = formattedDate, style = normalStyle)
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
            }
        }

    }

}