package com.example.imgstut

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.imgstut.databinding.ActivityMainBinding
import com.theartofdev.edmodo.cropper.CropImage
import java.net.URI
import android.os.Environment

import android.graphics.drawable.BitmapDrawable
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.R.attr.bitmap
import android.R.attr.publicKey
import java.util.*


class MainActivity : AppCompatActivity() {

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().setAspectRatio(16,9)
                .getIntent(this@MainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
          return CropImage.getActivityResult(intent)?.uri
        }
    }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private lateinit var binding: ActivityMainBinding
    internal var imagePath: String? = ""
    private val CAPTURE_PHOTO = 104



    companion object {
        private const val CAMERA_PERM_CODE = 101
        val IMAGE_REQUEST_CODE = 1_000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pickImageButton.setOnClickListener {
            pickImageFromGallery()
        }
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.CAMERA
                ), 100
            )
        }

        binding.btnCam.setOnClickListener(View.OnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 100)
        })

//edit start
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let { uri->
                binding.imageView.setImageURI(uri)
            }
        }

        binding.editBtn.setOnClickListener(View.OnClickListener {
            cropActivityResultLauncher.launch(null)
        })
    //edit end

        //save
        binding.btnSave.setOnClickListener{
        

        }
        //save

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            //get img cap
            val captureImage = data!!.extras!!["data"] as Bitmap?
            //set cap img into imgView
            binding.imageView.setImageBitmap(captureImage)
        }
        //gallery
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            binding.imageView.setImageURI(data?.data)
        }
        //save
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                CAPTURE_PHOTO -> {

                    val capturedBitmap = data?.extras!!.get("data") as Bitmap

                    //saveImage(capturedBitmap)
                    saveImage(capturedBitmap)
                    binding.imageView.setImageBitmap(capturedBitmap)
                }


                else -> {
                }
            }

            //save
        }

    }

    //save
    fun saveImage(finalBitmap: Bitmap) {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/capture_photo")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val OutletFname = "Image-$n.jpg"
        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            imagePath = file.absolutePath
            out.flush()
            out.close()


        } catch (e: Exception) {
            e.printStackTrace()

        }

    }


}