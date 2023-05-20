package com.example.kidsdrawingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.kidsdrawingapp.ui.theme.KidsDrawingAppTheme


class MainActivity : ComponentActivity() {
    lateinit var drawingView: DrawingView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KidsDrawingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(9.dp),
                    color = Color.White
                ) {
                    Column {
                        DrawingCanvas()
                        ToolArea()
                    }

                }
            }
        }
    }

    @Composable
    fun DrawingCanvas() {
        val configuration = LocalConfiguration.current
        val canvasHeight = (configuration.screenHeightDp * 0.85).dp

        Box(
            modifier = Modifier.height(canvasHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "canvas background image",
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(canvasHeight)
            )
            AndroidView(
                factory = {
                    drawingView = DrawingView(it)
                    drawingView.setSizeForBrush(20f)
                    drawingView
                },
                modifier = Modifier
                    .border(BorderStroke(0.5.dp, Color(0xFF9AA2AF)))
                    .height(canvasHeight)
                    .background(Color(0x80FFFFFF))
            )
        }

    }

    @Preview
    @Composable
    fun ToolArea() {
        val configuration = LocalConfiguration.current
        val toolHeight = (configuration.screenHeightDp * 0.1).dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(toolHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Palette()
            BrushDialogButton()
        }
    }

    @Composable
    fun Palette() {
        val currentColor = remember { mutableStateOf(Color.Black) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF999999), RoundedCornerShape(10.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            ColorButton(
                color = Color.Black,
                isActive = Color.Black == currentColor.value,
                onClick = {
                    currentColor.value = Color.Black
                    setColor(Color.Black)
                })
            ColorButton(color = Color.Red, isActive = Color.Red == currentColor.value, onClick = {
                currentColor.value = Color.Red
                setColor(Color.Red)
            })
            ColorButton(color = Color.Blue, isActive = Color.Blue == currentColor.value, onClick = {
                currentColor.value = Color.Blue
                setColor(Color.Blue)
            })
            ColorButton(
                color = Color.Yellow,
                isActive = Color.Yellow == currentColor.value,
                onClick = {
                    currentColor.value = Color.Yellow
                    setColor(Color.Yellow)
                })
            ColorButton(
                color = Color.Green,
                isActive = Color.Green == currentColor.value,
                onClick = {
                    currentColor.value = Color.Green
                    setColor(Color.Green)
                })
            ColorButton(
                color = Color.Magenta,
                isActive = Color.Magenta == currentColor.value,
                onClick = {
                    currentColor.value = Color.Magenta
                    setColor(Color.Magenta)
                })
        }
    }

    private fun setColor(color: Color) {
        val colorIntValue = color.toArgb()
        drawingView.setColorForBrush(colorIntValue)
    }

    @Composable
    fun ColorButton(color: Color, isActive: Boolean, onClick: () -> Unit) {
        IconButton(
            onClick = onClick
        ) {
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }


    @Composable
    fun BrushDialogButton() {
        val showDialog = remember { mutableStateOf(false) }
        if (showDialog.value) {
            BrushDialog(onDismiss = { showDialog.value = false })
        }
        IconButton(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_brush), contentDescription = "brush")
        }

    }

    @Composable
    fun BrushDialog(onDismiss: () -> Unit) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = {
                        drawingView.setSizeForBrush(10f)
                        onDismiss()
                    }) {
                        BrushCircle(size = BrushSize.SMALL)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    IconButton(onClick = {
                        drawingView.setSizeForBrush(20f)
                        onDismiss()
                    }) {
                        BrushCircle(size = BrushSize.MEDIUM)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    IconButton(onClick = {
                        drawingView.setSizeForBrush(30f)
                        onDismiss()
                    }) {
                        BrushCircle(size = BrushSize.BIG)
                    }
                }
            }

        }
    }
}

enum class BrushSize {
    SMALL,
    MEDIUM,
    BIG
}

@Composable
fun BrushCircle(size: BrushSize) {
    val sizeDp = when (size) {
        BrushSize.SMALL -> 10.dp
        BrushSize.MEDIUM -> 20.dp
        BrushSize.BIG -> 30.dp
    }
    Box(
        modifier = Modifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(Color(0xFF666666))
    )
}
