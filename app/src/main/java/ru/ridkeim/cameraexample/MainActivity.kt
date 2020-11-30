package ru.ridkeim.cameraexample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var imageFileUri: Uri? = null
    private lateinit var imageView: ImageView

    companion object{
        private const val CAMERA_REQUEST = 0
        private const val KEY_IMAGE_URI = "image_uri"
        private const val FILE_PROVIDER_AUTHORITY = "ru.ridkeim.cameraexample.fileprovider"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.cameraButton).setOnClickListener {
            dispatchTakePictureIntent()
        }
        imageView = findViewById<ImageView>(R.id.imageView)
        savedInstanceState?.getString(KEY_IMAGE_URI)?.let {
            imageFileUri = Uri.parse(it)
            imageView.setImageURI(imageFileUri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            imageView.setImageURI(imageFileUri)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imageFileUri?.let {
            outState.putString(KEY_IMAGE_URI, it.toString())
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile() : File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{ takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val imageFile = try {
                    createImageFile()
                } catch ( e : IOException){
                    null
                }
                imageFile?.also{
                    imageFileUri = FileProvider.getUriForFile(
                            this,
                            FILE_PROVIDER_AUTHORITY,
                            it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }


}