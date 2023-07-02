package com.example.babbage_pb_19.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.R.layout as LAYOUT
import com.example.babbage_pb_19.R.drawable as DRAWABLE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddActivity : AppCompatActivity() {

    private var storageRef = Firebase.storage
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var postPict: ImageView

    private var uriPostPict : Uri? = null
    private lateinit var camIntent:Intent
    private lateinit var galIntent:Intent
    private lateinit var cropIntent:Intent

    private val CAMERA_CODE = 101
    private val GALLERY_CODE = 102
    private val CROP_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT.activity_add)

        enableRuntimePermission()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageRef = FirebaseStorage.getInstance()
        postPict = findViewById(ID.image_post)

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Post")
        val postId = databaseReference.push().key
        var profileImg = ""
        var name = ""

        userRef.child(userId).get().addOnSuccessListener {it1 ->
            if (it1.exists()) {
                name = it1.child("name").value.toString()
                profileImg = it1.child("image").value.toString()
            }
        }
        findViewById<ImageView>(ID.image_post).setOnClickListener { openDialog() }

        findViewById<TextView>(ID.close_btn).setOnClickListener {
            startActivity(Intent(this@AddActivity, MainActivity::class.java))
        }
        findViewById<TextView>(ID.save_btn).setOnClickListener {

            storageRef.getReference("Posts Pict").child(System.currentTimeMillis().toString())
                .putFile(uriPostPict!!)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val caption = findViewById<EditText>(ID.caption_post).text.toString()
                            val judul = findViewById<EditText>(ID.judul_post).text.toString()
                            val dataMap = mapOf(
                                "postid" to postId,
                                "postpict" to uri.toString(),
                                "caption" to caption,
                                "judul" to judul,
                                "poster_uid" to userId,
                                "poster_img" to profileImg,
                                "poster_name" to name
                            )

                            databaseReference.child(postId!!).updateChildren(dataMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                                    val dbFirestore = Firebase.firestore
                                    dbFirestore.collection("Post").document().set(dataMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Save Successful to Firestore", Toast.LENGTH_SHORT).show()
                                            startActivity(
                                                Intent(
                                                    this@AddActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this@AddActivity)
        openDialog.setIcon(DRAWABLE.ic_image_teal)
        openDialog.setTitle("Choose your Image from")
        openDialog.setNegativeButton("Camera") { dialog, _ ->
            openCamera()
            dialog.dismiss()
        }
        openDialog.setPositiveButton("Gallery") { dialog, _ ->
            openGallery()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        openDialog.create()
        openDialog.show()
    }

    private fun openCamera() {
        camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(camIntent, CAMERA_CODE)
        }
    }

    private fun openGallery() {
        galIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(galIntent,
            "Select Image From Gallery "),GALLERY_CODE)
    }

    private fun enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@AddActivity,Manifest.permission.CAMERA
            )){
            Toast.makeText(this@AddActivity,
                "Camera Permission for using the Camera",
                Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this@AddActivity,
                arrayOf(Manifest.permission.CAMERA), RequestPermissionCode
            )
        }
    }

    private fun cropImages(){
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uriPostPict,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",900)
            cropIntent.putExtra("outputY",600)
            cropIntent.putExtra("aspectX",9)
            cropIntent.putExtra("aspectY",6)
            cropIntent.putExtra("scale", true)
            cropIntent.putExtra("return-data", true)
            startActivityForResult(cropIntent,CROP_CODE)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this, "Error Occurred ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.getParcelableExtra<Bitmap>("data")
            if (imageBitmap != null) {
                uriPostPict = bitmapToUri(this, imageBitmap)
                cropImages()
                postPict.setImageURI(uriPostPict)
            }
        } else if (requestCode == GALLERY_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                uriPostPict = data.data!!
                cropImages()
                postPict.setImageURI(uriPostPict)
            }
        } else if (requestCode == CROP_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val bundle = data.extras
                val bitmap = bundle?.getParcelable<Bitmap>("data")
                if (bitmap != null) {
                    uriPostPict = bitmapToUri(this, bitmap)
                    postPict.setImageURI(uriPostPict)
                }
            }
        }
    }
    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val contentResolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        var uri: Uri? = null
        try {
            val imageUri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                val outputStream = contentResolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                    uri = imageUri
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error Occurred ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }

        return uri
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.isNotEmpty()
                && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@AddActivity,
                    "Permission Granted, Now you can access Camera",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@AddActivity,
                    "Permission Not Granted, You can not access Camera",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object{
        const val RequestPermissionCode = 111
    }
}