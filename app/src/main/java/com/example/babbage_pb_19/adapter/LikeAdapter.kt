package com.example.babbage_pb_19.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class LikeAdapter() : RecyclerView.Adapter<LikeAdapter.MyViewHolder>() {

    private val postList = ArrayList<Post>()
    private val mylikes : MutableList<String> = mutableListOf()
    private var firebaseUser = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.post_item,
            parent,false
        )
        return MyViewHolder(itemView)

    }

    private fun isLikes(postId: String, imageView: ImageView) {
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var databaseReference = FirebaseDatabase.getInstance().reference.child("Likes").child(firebaseUser!!.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mendapatkan nilai terbaru dari database
                if (dataSnapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.heart_filled)
                    imageView.tag = "Liked"
                } else {
                    imageView.setImageResource(R.drawable.heart_transparant)
                    imageView.tag = "Like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Jika terjadi error saat membaca database
                println("Error: ${databaseError.message}")
            }
        })
    }

    open fun myLikes() {
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var databaseReference = FirebaseDatabase.getInstance().reference.child("Likes").child(firebaseUser!!.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mendapatkan nilai terbaru dari database
                for (snapshot in dataSnapshot.children) {
                    val key = snapshot.key.toString()
                    key.let { mylikes.add(it) }
                }
                println(mylikes.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Jika terjadi error saat membaca database
                println("Error: ${databaseError.message}")
            }
        })

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = postList[position]
        //Meletakan Photo Profile
        if(mylikes.contains(currentitem.postid)) {
            holder.cardHolder.visibility = View.VISIBLE
            val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is signed in
                userRef.child(currentitem.poster_uid.toString()).get().addOnSuccessListener {
                    if (it.exists()) {
                        //Meletakan Photo Profile
                        Picasso.get()
                            .load(it.child("image").value.toString())
                            .placeholder(R.drawable.homebliss)
                            .error(R.drawable.homebliss)
                            .into(holder.profileImage)
                    }
                }
            } else {
                // No user is signed in
            }

            //Meletakan Postingan
            Picasso.get()
                .load(currentitem.postpict)
                .placeholder(com.example.babbage_pb_19.R.drawable.ic_image_teal)
                .error(com.example.babbage_pb_19.R.drawable.ic_image_teal)
                .into(holder.postImage)

            holder.name1.text = currentitem.poster_name
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
        }else {
            holder.cardHolder.visibility = View.GONE
            holder.cardHolder.layoutParams = RecyclerView.LayoutParams(0, 0)
        }

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

        var cardHolder : CardView = itemView.findViewById(R.id.cardholder)
        var profileImage : CircleImageView = itemView.findViewById(R.id.user_profile)
        var postImage : ImageView = itemView.findViewById(R.id.post_image_home)
        var likeBtn : ImageView = itemView.findViewById(R.id.post_image_like_btn)
        var judul : TextView = itemView.findViewById(R.id.judul)
        var discussionBtn : ImageView = itemView.findViewById(R.id.post_image_discuss_btn)
        var name1 : TextView = itemView.findViewById(R.id.user_name_search)
        var caption : TextView = itemView.findViewById(R.id.description)

    }
}