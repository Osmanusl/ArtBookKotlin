package com.osmanuslu.artbookkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.osmanuslu.artbookkotlin.databinding.ActivityArtBinding
import com.osmanuslu.artbookkotlin.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class ArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()




    }

    fun saveButtonClick(view: View) {
        val artName=binding.artNameText.text.toString()
        val artistName=binding.artistNameText.text.toString()
        val year =binding.yearText.text.toString()




        if(selectedBitmap !=null){
            val smallBitmap=makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream= ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray=outputStream.toByteArray()

            try{
                val database=this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB)")
                    val sqlString="INSERT INTO arts(artname,artistname,year,image)VALUES(?,?,?,?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,artName)
                    statement.bindString(2,artistName)
                    statement.bindString(3,year)
                    statement.bindBlob(4,byteArray)
                    statement.execute()



            }catch (e:Exception){
               e.printStackTrace()
            }
            val intent =Intent(this@ArtActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)


        }


    }
    private fun makeSmallerBitmap(image:Bitmap,maximumSize:Int):Bitmap{
        var width=image.width
        var height=image.height

        val bitmapratio : Double=width.toDouble()/height.toDouble()

        if(bitmapratio>1){
            width =maximumSize
            val scaledHeight=width /bitmapratio
            height=scaledHeight.toInt()

        }else{

            height=maximumSize
            val scaledWidth=height *bitmapratio
            width =scaledWidth.toInt()

        }

        return Bitmap.createScaledBitmap(image,100,100,true)
    }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                Snackbar.make(view, "permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


                    }).show()

            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }


        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


        }


    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        //binding.imageView.setImageURI(imageData)
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        this@ArtActivity.contentResolver,
                                        imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }
                }

            }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }else{
                Toast.makeText(this@ArtActivity,"permission nedeed",Toast.LENGTH_LONG).show()
            }


        }
    }
}
