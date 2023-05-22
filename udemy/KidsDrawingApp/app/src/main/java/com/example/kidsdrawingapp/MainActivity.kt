package com.example.kidsdrawingapp

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kidsdrawingapp.ui.theme.KidsDrawingAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.compose.runtime.livedata.observeAsState


class MainActivity : ComponentActivity() {
    lateinit var drawingView: DrawingView
    lateinit var imageView: ImageView
    lateinit var dialogViewModel: MyViewModel


    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageView.setImageURI(result.data?.data)
            }
        }
    private val requestPermissions: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            /**
            Here it returns a Map of permission name as key with boolean as value
            We loop through the map to get the value we need which is the boolean
            value
             */
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    Toast.makeText(
                        this,
                        "Permission granted now yo can read the storage files.",
                        Toast.LENGTH_LONG
                    ).show()

                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    if (permissionName == permission.READ_EXTERNAL_STORAGE) {
                        openGalleryLauncher.launch(
                            pickIntent
                        )
                    }

                } else {
                    if (permissionName == permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Oops, you just denied the permission.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

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
                        Row(modifier = Modifier.weight(8.5f, true)) {
                            DrawingCanvas()
                        }
                        Row(modifier = Modifier.weight(1.5f, true)) {
                            ToolArea()
                        }
                    }



                    dialogViewModel = viewModel()

                    val openDialog by dialogViewModel.open.observeAsState(false)

                    if (openDialog) {
                        LoadingDialog()
                    }

                }
            }
        }
    }

    @Preview
    @Composable
    fun LoadingDialog() {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                    )
                    Text("Please wait...")
                }
            }
        }
    }

    @Composable
    fun DrawingCanvas() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            AndroidView(
                factory = {
                    imageView = ImageView(it)
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView
                },
                modifier = Modifier.fillMaxHeight(),
            )
            AndroidView(
                factory = {
                    drawingView = DrawingView(it)
                    drawingView.setSizeForBrush(20f)
                    drawingView
                },
                modifier = Modifier
                    .border(BorderStroke(0.5.dp, Color(0xFF9AA2AF)))
                    .fillMaxHeight()
                    .background(Color(0x80FFFFFF))
            )
        }
    }

    @Preview
    @Composable
    fun ToolArea() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.weight(1f, true)) {
                Palette()
            }
            Row(modifier = Modifier.weight(1f, true)) {
                Tools()
            }
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
    fun Tools() {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            GalleryButton()
            BrushDialogButton()
            UndoButton()
            RedoButton()
            SaveButton()
        }
    }

    @Composable
    fun GalleryButton() {
        IconButton(
            onClick = {
                requestStoragePermission()
            }, modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_gallery),
                contentDescription = "gallery",
                tint = Color.Unspecified
            )
        }
    }

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog(
                " Kids Drawing App",
                "Kids Drawing App needs to Access Your External Storage"
            )
        } else {
            requestPermissions.launch(
                arrayOf(
                    permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun requestWriteStoragePermission() {
        if (shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)) {
            showRationaleDialog(
                " Kids Drawing App",
                "Kids Drawing App needs to Access Your External Storage"
            )
        } else {
            requestPermissions.launch(
                arrayOf(
                    permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    /**
     * Shows rationale dialog for displaying why the app needs permission
     * Only shown if the user has denied the permission request previously
     */
    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
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
            Icon(
                painter = painterResource(id = R.drawable.ic_brush),
                contentDescription = "brush",
                tint = Color.Unspecified
            )
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

    @Composable
    fun UndoButton() {
        IconButton(
            onClick = {
                drawingView.undoPath()
            },
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_undo),
                contentDescription = "undo",
                tint = Color.Unspecified
            )
        }
    }

    @Composable
    fun RedoButton() {
        IconButton(
            onClick = {
                drawingView.redoPath()
            },
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_undo),
                modifier = Modifier.rotate(180f),
                contentDescription = "redo",
                tint = Color.Unspecified
            )
        }
    }

    @Composable
    fun SaveButton() {
        IconButton(
            onClick = {
                Log.i("save", "onClick!")
                if (isReadStorageAllowed()) {
                    Log.i("save", "allowed")
                    dialogViewModel.openDialog()
                    lifecycleScope.launch {
                        val bitmap = getBitmapFromView()
                        saveBitmapFile(bitmap)
                    }
                } else {
                    requestWriteStoragePermission()
                }
            },
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_save),
                contentDescription = "save",
                tint = Color.Unspecified
            )
        }
    }

    /**
     * We are calling this method to check the permission status
     */
    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this, permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Create bitmap from view and returns it
     */
    private fun getBitmapFromView(): Bitmap {

        //Define a bitmap with the same size as the view.
        // CreateBitmap : Returns a mutable bitmap with the specified width and height
        val returnedBitmap =
            Bitmap.createBitmap(drawingView.width, drawingView.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = imageView.drawable
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            imageView.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(android.graphics.Color.WHITE)
        }
        // draw the view on the canvas
        drawingView.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val uniqueFileName = (System.currentTimeMillis() / 1000).toString() + ".png"
                    val file =
                        File(externalCacheDir?.absoluteFile.toString() + File.separator + "KidsDrawingApp_" + uniqueFileName)
                    val fo = FileOutputStream(file)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = file.absolutePath

                    runOnUiThread {
                        dialogViewModel.closeDialog()
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File saved successfully: $result",
                                Toast.LENGTH_LONG
                            ).show()
                            shareImage(result)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Something went wrong while saving the file.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun shareImage(result:String){

        /*MediaScannerConnection provides a way for applications to pass a
        newly created or downloaded media file to the media scanner service.
        The media scanner service will read metadata from the file and add
        the file to the media content provider.
        The MediaScannerConnectionClient provides an interface for the
        media scanner service to return the Uri for a newly scanned file
        to the client of the MediaScannerConnection class.*/

        /*scanFile is used to scan the file when the connection is established with MediaScanner.*/
        MediaScannerConnection.scanFile(
            this@MainActivity, arrayOf(result), null
        ) { path, uri ->
            // This is used for sharing the image after it has being stored in the storage.
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                uri
            ) // A content: URI holding a stream of data associated with the Intent, used to supply the data being sent.
            shareIntent.type =
                "image/png" // The MIME type of the data being handled by this intent.
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share"
                )
            )// Activity Action: Display an activity chooser,
            // allowing the user to pick what they want to before proceeding.
            // This can be used as an alternative to the standard activity picker
            // that is displayed by the system when you try to start an activity with multiple possible matches,
            // with these differences in behavior:
        }
        // END
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

class MyViewModel : ViewModel() {
    // Dialog box
    var open = MutableLiveData<Boolean>()

    fun openDialog() {
        open.value = true
    }

    fun closeDialog() {
        open.value = false
    }
}