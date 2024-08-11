package com.example.bookstoreproject.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header (
    labelLeft : String,
    labelRight: String,
    isAdd: Boolean = false,
    isLogin : Boolean = false,
    isLogout : Boolean = false,
    isBack: Boolean = false,
    isBook: Boolean = false,
    isView: Boolean = false,
    isReset: Boolean = false,
    onLogout: (() -> Unit)? = null,
    onAddbook: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onCreate: (() -> Unit)? = null,
    onReset: (() -> Unit)? = null,
    //UI
    labelRightColor: Color? = null,
    headerTitle: String = ""
) {



    val headerStyle = TextStyle(
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    val btnStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )

    val rightButtonColor = labelRightColor ?: Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(
                top = 40.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLogin) {
            // Display only the title
            Text(text = "CK Book Store", style = headerStyle)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Left Btn
                Button(
                    enabled = true,
                    onClick = {
                        Log.d("Header", "Log Out")

                        //if logout btn is click then pass back value to the Homepage....
                        if (isLogout) {
                            onLogout?.invoke()
                        } else if(isBook || isView) {
                            onBack?.invoke()
                        } else if (isReset && !isView) {
                            onBack?.invoke()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    )
                )
                {
                    Text(text = labelLeft, style = btnStyle)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = when {
                        isBook -> "Create New"
                        isView || isReset -> {
                            if (headerTitle.length > 12) "${headerTitle.take(12)}..."
                            else headerTitle
                        }
                        else -> "CK Book Store"
                    },
                    style = headerStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))

                //Right Btn

                    Button(
                        enabled = true,
                        onClick = {
                            if (isAdd) {
                                //homepage add button
                                onAddbook?.invoke()
                            } else if (isView) {
                                //viewEdit page edit button
                                onEdit?.invoke()
                            } else if (isBook) {
                                //create page done button
                                onCreate?.invoke()
                            } else if (isReset) {
                                onReset?.invoke()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = rightButtonColor,
                        )
                    )
                    {
                        if (isAdd)
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        else
                            Text(text = labelRight, style = btnStyle)
                    }


            }
        }
    }
}