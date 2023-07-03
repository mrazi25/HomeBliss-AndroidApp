package com.example.babbage_pb_19.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.R.layout as LAYOUT
import com.example.babbage_pb_19.R.drawable as DRAWABLE
import com.example.babbage_pb_19.adapters.DiscussionAdapter
import com.example.babbage_pb_19.datas.Discussion
import com.example.babbage_pb_19.viewmodels.DiscussionViewModel
import com.example.babbage_pb_19.datas.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DiscussionActivity : AppCompatActivity() {


    private lateinit var discussionRecyclerView: RecyclerView
    private lateinit var viewModel : DiscussionViewModel
    private lateinit var adapter1: DiscussionAdapter
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT.activity_discussion)

        discussionRecyclerView = findViewById(ID.comments_recycler_view)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        discussionRecyclerView.layoutManager = linearLayoutManager
        discussionRecyclerView.setHasFixedSize(true)
        adapter1 = DiscussionAdapter()
        discussionRecyclerView.adapter = adapter1

        viewModel = ViewModelProvider(this)[DiscussionViewModel::class.java]
        var intent = intent
        post = (intent.getSerializableExtra("postToDiscuss") as? Post)!!

        showPost()
        viewModel.loadDiscussions(post.postid.toString())
        viewModel.allDiscussion.observe(this) { discussions ->
            adapter1.updateDiscussionList(discussions)
        }
        findViewById<ImageButton>(ID.submit_button).setOnClickListener {
            if (findViewById<EditText>(ID.comment_input).text.equals("")){
                Toast.makeText(this, "Say something in discussion.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                addDiscussion()
            }

        }

        findViewById<TextView>(ID.back_btn).setOnClickListener {
            startActivity(Intent(this@DiscussionActivity, MainActivity::class.java))
        }

    }

    fun showPost() {
        Picasso.get().load(post.postpict)
            .placeholder(DRAWABLE.ic_image_teal)
            .error(DRAWABLE.ic_image_teal)
            .into(findViewById<ImageView>(ID.post_image_discuss))
    }

    fun addDiscussion() {
        var dbRef = FirebaseDatabase.getInstance().reference.child("Discussion").child(post.postid.toString())
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            userRef.child(user.uid).get().addOnSuccessListener { it ->
                if (it.exists()) {
                    var discussion = Discussion(findViewById<EditText>(ID.comment_input).text.toString(),
                        user?.uid, it.child("image").value.toString())
                    dbRef.push().setValue(discussion)
                        .addOnSuccessListener {
                            println("Adding new discussion is success")
                            findViewById<EditText>(ID.comment_input).setText("")
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
        } else {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT)
                .show()
            println("There is no user that currently logging in")
        }
    }
}
