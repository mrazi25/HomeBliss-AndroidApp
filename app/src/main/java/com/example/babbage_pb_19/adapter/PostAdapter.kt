package com.example.babbage_pb_19.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.R.layout as LAYOUT
import com.example.babbage_pb_19.R.drawable as DRAWABLE
import com.example.babbage_pb_19.activity.DiscussionActivity
import com.example.babbage_pb_19.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private val postList = ArrayList<Post>()
    private var firebaseUser = FirebaseAuth.getInstance().currentUser
    private lateinit var parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            LAYOUT.post_item,
            parent,false
        )
        this.parent = parent
        return MyViewHolder(itemView)

    }
    private fun isLikes(postId: String, imageView: ImageView) {
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var databaseReference = FirebaseDatabase.getInstance().reference.child("Likes").child(firebaseUser!!.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    imageView.setImageResource(DRAWABLE.heart_filled)
                    imageView.tag = "Liked"
                } else {
                    imageView.setImageResource(DRAWABLE.heart_transparant)
                    imageView.tag = "Like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(parent.context, "Error Occurred", Toast.LENGTH_SHORT)
                    .show()
                println("Cannot liking and unliking the post")
            }
        })
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = postList[position]

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userRef.child(currentitem.poster_uid.toString()).get().addOnSuccessListener {
                if (it.exists()) {
                    Picasso.get()
                        .load(it.child("image").value.toString())
                        .placeholder(DRAWABLE.homebliss)
                        .error(DRAWABLE.homebliss)
                        .into(holder.profileImage)
                    holder.name1.text = it.child("name").value.toString()
                }
            }
        } else {
            Toast.makeText(parent.context, "Error Occurred", Toast.LENGTH_SHORT)
                .show()
            println("Cannot get the image path")
        }

        //Meletakan Postingan
        Picasso.get()
            .load(currentitem.postpict)
            .placeholder(DRAWABLE.ic_image_teal)
            .error(DRAWABLE.ic_image_teal)
            .into(holder.postImage)

        holder.caption.text = currentitem.caption
        holder.judul.text = currentitem.judul

        isLikes(currentitem.postid.toString(), holder.likeBtn)

        holder.likeBtn.setOnClickListener {
                if (holder.likeBtn.tag.equals("Like")) {
                    FirebaseDatabase.getInstance().reference.child("Likes")
                        .child(firebaseUser!!.uid)
                        .child(currentitem.postid.toString()).setValue(ServerValue.TIMESTAMP)
                } else {
                    FirebaseDatabase.getInstance().reference.child("Likes")
                        .child(firebaseUser!!.uid)
                        .child(currentitem.postid.toString()).removeValue()
                }

        }
        holder.discussionBtn.setOnClickListener {
            var postToDiscuss=currentitem
            val intent = Intent(parent.context, DiscussionActivity::class.java)
            intent.putExtra("postToDiscuss", currentitem)
            parent.context.startActivity(intent)
        }
    }

    private var data: List<Post> = emptyList()

    fun setData(data: List<Post>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun updatePostList(postList : List<Post>){

        this.postList.clear()
        this.postList.addAll(postList)
        notifyDataSetChanged()

    }
    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var profileImage : CircleImageView = itemView.findViewById(ID.user_profile)
        var postImage : ImageView = itemView.findViewById(ID.post_image_home)
        var likeBtn : ImageView = itemView.findViewById(ID.post_image_like_btn)
        var judul : TextView = itemView.findViewById(ID.judul)
        var discussionBtn : ImageView = itemView.findViewById(ID.post_image_discuss_btn)
        var name1 : TextView = itemView.findViewById(ID.user_name_search)
        var caption : TextView = itemView.findViewById(ID.description)
        
    }

}
