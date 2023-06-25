package com.example.babbage_pb_19.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.babbage_pb_19.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var file: File
    private lateinit var uri : Uri
    private lateinit var camIntent:Intent
    private lateinit var galIntent:Intent
    private lateinit var cropIntent:Intent

    private lateinit var btnChangeIMG: TextView
    private lateinit var profileIMG: CircleImageView

    private var storageRef = Firebase.storage
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        userInfo()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        storageRef = FirebaseStorage.getInstance()

        val nameTextView = findViewById<EditText>(R.id.name_fill)
        val emailTextView = findViewById<EditText>(R.id.email_fill)

        profileIMG = findViewById(R.id.profile_image)
        btnChangeIMG = findViewById(R.id.change_image_btn)
        enableRuntimePermission()

        profileIMG.setOnClickListener { openDialog() }
        btnChangeIMG.setOnClickListener { openDialog() }

        val closebtn = findViewById<ImageView>(R.id.close_btn)
        closebtn.setOnClickListener {
            startActivity(Intent(this@ProfileSettingActivity, MainActivity::class.java))
        }

        val savebtn = findViewById<ImageView>(R.id.save_btn)
        savebtn.setOnClickListener {

        val name = nameTextView.text.toString()
        val email = emailTextView.text.toString()


        storageRef.getReference("images").child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid
                        val mapImage = mapOf(
                            "image" to it.toString(),
                            "name" to name,
                            "email" to email
                        )

                        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        databaseReference.child(userId).updateChildren(mapImage)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                                startActivity(
                                    Intent(
                                        this@ProfileSettingActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                            .addOnFailureListener {error ->
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                    }

                }
        }

    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val user = FirebaseAuth.getInstance().currentUser
        val nameTextView = findViewById<EditText>(R.id.name_fill)
        val emailTextView = findViewById<EditText>(R.id.email_fill)
        val pictureCIV = findViewById<CircleImageView>(R.id.profile_image)

        if (user != null) {
            // User is signed in
            userRef.child(user.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    nameTextView.setText(it.child("name").value.toString())
                    emailTextView.setText(it.child("email").value.toString())
                    Picasso.get()
                        .load(it.child("image").value.toString())
                        .placeholder(R.drawable.homebliss)
                        .error(R.drawable.homebliss)
                        .into(pictureCIV)

                }
            }
        } else {
            // No user is signed in
        }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this@ProfileSettingActivity)
        openDialog.setIcon(R.drawable.ic_image_teal)
        openDialog.setTitle("Choose your Image from")
        openDialog.setNegativeButton("Camera"){
                dialog,_->
            openGallery()
            dialog.dismiss()
        }
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

    private fun openGallery() {
        galIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(galIntent,
            "Select Image From Gallery "),2)
    }

    private fun enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@ProfileSettingActivity,Manifest.permission.CAMERA
            )){
            Toast.makeText(this@ProfileSettingActivity,
                "Camera Permission allows us to Camera App",
                Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this@ProfileSettingActivity,
                arrayOf(Manifest.permission.CAMERA), RequestPermissionCode
            )
        }
    }

    private fun cropImages(){
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",180)
            cropIntent.putExtra("outputY",180)
            cropIntent.putExtra("aspectX",3)
            cropIntent.putExtra("aspectY",4)
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("return-data",true)
            startActivityForResult(cropIntent,1)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
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
            }
        }
        else if (requestCode == 1){
            if (data != null){
                val bundle = data.extras
                val bitmap = bundle!!.getParcelable<Bitmap>("data")
                profileIMG.setImageBitmap(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size>0
                && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@ProfileSettingActivity,
                    "Permission Granted , Now your application can access Camera",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@ProfileSettingActivity,
                    "Permission Granted , Now your application can not  access Camera",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object{
        const val RequestPermissionCode = 111
    }
}

