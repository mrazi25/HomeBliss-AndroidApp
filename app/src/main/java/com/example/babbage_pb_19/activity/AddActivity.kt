package com.example.babbage_pb_19.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.babbage_pb_19.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File

class AddActivity : AppCompatActivity() {

    private var storageRef = Firebase.storage
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var file: File
    private lateinit var uri : Uri
    private lateinit var camIntent:Intent
    private lateinit var galIntent:Intent
    private lateinit var cropIntent:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        enableRuntimePermission()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageRef = FirebaseStorage.getInstance()
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Post")

        val userRefFirestore = db.collection("Users")
        val databaseReferenceFirestore = db.collection("Post")

        val postId = databaseReference.push().key
        var profileImg = ""
        var name = ""

        userRef.child(userId).get().addOnSuccessListener {it1 ->
            if (it1.exists()) {
                name = it1.child("name").value.toString()
                profileImg = it1.child("image").value.toString()
            }
        }
        findViewById<ImageView>(R.id.image_post).setOnClickListener { openDialog() }

        findViewById<TextView>(R.id.close_btn).setOnClickListener {
            startActivity(Intent(this@AddActivity, MainActivity::class.java))
        }
        findViewById<TextView>(R.id.save_btn).setOnClickListener {

            storageRef.getReference("Posts Pict").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            val caption = findViewById<EditText>(R.id.caption_post).text.toString()
                            val judul = findViewById<EditText>(R.id.judul_post).text.toString()
                            val dataMap = mapOf(
                                "postid" to postId,
                                "postpict" to it.toString(),
                                "caption" to caption,
                                "judul" to judul,
                                "poster_uid" to userId,
                                "poster_img" to profileImg,
                                "poster_name" to name
                            )

                            databaseReference.child(postId!!).updateChildren(dataMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                                    var dbFirestore = Firebase.firestore
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
                                        .addOnFailureListener { error ->
                                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                                }
                        }

                }
        }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this@AddActivity)
        openDialog.setIcon(R.drawable.ic_image_teal)
        openDialog.setTitle("Choose your Image from")
        openDialog.setNegativeButton("Gallery"){
                dialog,_->
            openGallery()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        openDialog.create()
        openDialog.show()

    }

    private fun cropImages(){
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",180)
            cropIntent.putExtra("outputY",180)
            cropIntent.putExtra("aspectX",4)
            cropIntent.putExtra("aspectY",4)
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("scaleLeftIfNeeded", true)
            cropIntent.putExtra("return-data",true)
            startActivityForResult(cropIntent,1)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    private fun openGallery() {
        galIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(galIntent,
            "Select Image From Gallery "),2)
    }

    private fun enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@AddActivity, Manifest.permission.CAMERA
            )){
            Toast.makeText(this@AddActivity,
                "Camera Permission allows us to Camera App",
                Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this@AddActivity,
                arrayOf(Manifest.permission.CAMERA), RequestPermissionCode
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK){
            cropImages()
        } else if (requestCode == 2){
            if (data != null){
                uri = data.data!!
                cropImages()
                findViewById<ImageView>(R.id.image_post).setImageURI(uri)
            }
        }
        else if (requestCode == 1){
            if (data != null){
                val bundle = data.extras
                val bitmap = bundle!!.getParcelable<Bitmap>("data")
                findViewById<ImageView>(R.id.image_post).setImageBitmap(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size>0
                && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@AddActivity,
                    "Permission Granted , Now your application can access Camera",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@AddActivity,
                    "Permission Granted , Now your application can not  access Camera",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object{
        const val RequestPermissionCode = 111
    }
}