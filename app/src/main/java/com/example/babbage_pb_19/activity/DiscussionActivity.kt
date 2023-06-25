package com.example.babbage_pb_19.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.adapter.DiscussionAdapter
import com.example.babbage_pb_19.data.Discussion
import com.example.babbage_pb_19.data.DiscussionViewModel
import com.example.babbage_pb_19.data.Post
import com.example.babbage_pb_19.data.PostViewModel
import com.example.babbage_pb_19.repository.DiscussionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DiscussionActivity : AppCompatActivity() {

    private lateinit var discussionRecyclerView: RecyclerView
    lateinit var adapter1: DiscussionAdapter
    private lateinit var viewModel : DiscussionViewModel
    var listDiscussion = ArrayList<Discussion>()
    lateinit var post: Post


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)

        discussionRecyclerView = findViewById(R.id.comments_recycler_view)

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

        println(post.postid)
        viewModel.loadDiscussions(post.postid.toString())
        viewModel.allDiscussion.observe(this) { discussions ->
            adapter1.updateDiscussionList(discussions)
        }




        findViewById<ImageButton>(R.id.submit_button).setOnClickListener {
            if (findViewById<EditText>(R.id.comment_input).text.equals("")){
                Toast.makeText(this, "Say something in discussion. Don't send nothing!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                addDiscussion()
            }

        }

    }

    fun addDiscussion() {
        var dbRef = FirebaseDatabase.getInstance().reference.child("Discussion").child(post.postid.toString())
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val user = FirebaseAuth.getInstance().currentUser
        var img =""
        if (user != null) {
            // User is signed in
            userRef.child(user.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    var discussion = Discussion(findViewById<EditText>(R.id.comment_input).text.toString(),
                        user?.uid, it.child("image").value.toString())

                    dbRef.push().setValue(discussion)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT)
                                .show()
                            findViewById<EditText>(R.id.comment_input).setText("")
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                }
            }
        } else {
            // No user is signed in
        }
    }

    fun readDiscussion() {
        var dbRef = FirebaseDatabase.getInstance().getReference("Discussion").child(post.postid.toString())

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listDiscussion.clear()
                for (snapshot in snapshot.getChildren()) {
                    val discussion = snapshot.getValue(Discussion::class.java)
                    if (discussion != null) {
                        listDiscussion.add(discussion)
                    }
                }
                adapter1.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
    }

}