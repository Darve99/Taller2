package com.example.taller2

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.InputStream
import android.graphics.Matrix

class CamaraActivity : AppCompatActivity()
{
    private lateinit var foto: ImageView
    private val STORAGE_PERMISSION_REQUEST_CODE = 1
    val IMAGE_PICKER_REQUEST = 2
    val CAMARA_REQUEST = 3

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)

        val botonGaleria = findViewById<Button>(R.id.buttonGaleria)
        val botonCamara = findViewById<Button>(R.id.buttonCamara)
        foto = findViewById<ImageView>(R.id.imagen)

        botonGaleria.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicitar el permiso si no está concedido
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                // Si el permiso está concedido sigue
                selectPicture()
            }
        }

        botonCamara.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicitar el permiso si no está concedido
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMARA_REQUEST
                )
            } else {
                // Si el permiso está concedido sigue
                takePicture()
            }
        }

        //
    }

    private fun selectPicture() {
        val pickImage = Intent(Intent.ACTION_PICK)
        pickImage.type = "image/*"
        startActivityForResult(pickImage,IMAGE_PICKER_REQUEST)
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, CAMARA_REQUEST)
        } catch (e: ActivityNotFoundException) {
            e.message?.let { Log.e("PERMISSION_APP", it) }
        }
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IMAGE_PICKER_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val imageUri = data?.data
                        val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        foto.setImageBitmap(selectedImage)
                    } catch (e: Exception) {
                        e.message?.let { Log.e("PERMISSION_APP", it) }
                    }
                }
            }

            CAMARA_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val extras: Bundle? = data?.extras
                        val imageBitmap = extras?.get("data") as? Bitmap
                        val rotatedBitmap = rotateImage(imageBitmap, 90f)
                        foto.setImageBitmap(rotatedBitmap)
                    } catch (e: Exception) {
                        e.message?.let { Log.e("PERMISSION_APP", it) }
                    }
                }
            }
        }
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        if (source == null)
            return null

        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}