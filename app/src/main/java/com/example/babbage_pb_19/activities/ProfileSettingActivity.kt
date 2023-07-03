package com.example.babbage_pb_19.activities

import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.*
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

class ProfileSettingActivity : AppCompatActivity() {

    private var uri_profile_pict: Uri? = null
    private lateinit var camIntent:Intent
    private lateinit var galIntent:Intent
    private lateinit var cropIntent:Intent

    private lateinit var btnChangeIMG: TextView
    private lateinit var profileIMG: CircleImageView

    private var storageRef = Firebase.storage
    private lateinit var firebaseUser: FirebaseUser

    private val CAMERA_CODE = 101
    private val GALLERY_CODE = 102
    private val CROP_CODE = 100

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

        findViewById<ImageView>(R.id.close_btn).setOnClickListener {
            startActivity(Intent(this@ProfileSettingActivity, MainActivity::class.java))
        }

        findViewById<ImageView>(R.id.save_btn).setOnClickListener {

            val name = nameTextView.text.toString()
            val email = emailTextView.text.toString()


            storageRef.getReference("images").child(System.currentTimeMillis().toString())
                .putFile(uri_profile_pict!!)
                .addOnSuccessListener{ task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid
                            val mapImage = mapOf(
                                "image" to uri.toString(),
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

        findViewById<Button>(R.id.delete_btn).setOnClickListener {
            firebaseUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Your Account Have Been Deactivate", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ProfileSettingActivity, LoginActivity::class.java))
                    }
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                        .show()
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
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this@ProfileSettingActivity)
        openDialog.setIcon(R.drawable.ic_image_teal)
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
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri_profile_pict,"image/*")
            cropIntent.putExtra("crop", true)
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("outputX", 300)
            cropIntent.putExtra("outputY", 300)
            cropIntent.putExtra("circleCrop", "true")
            cropIntent.putExtra("return-data", true)
            startActivityForResult(cropIntent,CROP_CODE)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.getParcelableExtra<Bitmap>("data")
            if (imageBitmap != null) {
                uri_profile_pict = bitmapToUri(this, imageBitmap)
                cropImages()
                profileIMG.setImageURI(uri_profile_pict)
            }
        } else if (requestCode == GALLERY_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                uri_profile_pict = data.data!!
                cropImages()
                profileIMG.setImageURI(uri_profile_pict)
            }
        } else if (requestCode == CROP_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val bundle = data.extras
                val bitmap = bundle?.getParcelable<Bitmap>("data")
                if (bitmap != null) {
                    uri_profile_pict = bitmapToUri(this, bitmap)
                    profileIMG.setImageURI(uri_profile_pict)
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
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.close()
                    uri = imageUri
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.isNotEmpty()
                && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@ProfileSettingActivity,
                    "Permission Granted, Now your application can access Camera",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@ProfileSettingActivity,
                    "Permission Not Granted, Your application can not access Camera",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object{
        const val RequestPermissionCode = 111
    }
}