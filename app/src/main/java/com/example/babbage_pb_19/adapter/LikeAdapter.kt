package com.example.babbage_pb_19.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.R.layout as LAYOUT
import com.example.babbage_pb_19.R.drawable as DRAWABLE
import com.example.babbage_pb_19.activity.DiscussionActivity
import com.example.babbage_pb_19.data.Like
import com.example.babbage_pb_19.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class LikeAdapter() : RecyclerView.Adapter<LikeAdapter.MyViewHolder>() {

    private val postList = ArrayList<Post>()
    private val likeList: MutableMap<String, Long> = mutableMapOf()
    private val mylikes = ArrayList<Pair<String, Long>>()
    private val idPostThatILike = ArrayList<String>()
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
                // Mendapatkan nilai terbaru dari database
                if (dataSnapshot.child(postId).exists()) {
                    imageView.setImageResource(DRAWABLE.heart_filled)
                    imageView.tag = "Liked"
                } else {
                    imageView.setImageResource(DRAWABLE.heart_transparant)
                    imageView.tag = "Like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }
//    open fun myLikes() {
//        var firebaseUser = FirebaseAuth.getInstance().currentUser
//        var databaseReference = FirebaseDatabase.getInstance().reference
//            .child("Likes")
//            .child(firebaseUser!!.uid)
//            .orderByValue().limitToLast(10)
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (snapshot in dataSnapshot.children) {
//                    val userId = snapshot.key
//                    val timestamp = snapshot.value as Long
//                    mylikes.add(Pair(userId, timestamp) as Pair<String, Long>)
//                }
//
//                // Melakukan pengurutan data secara descending berdasarkan timestamp
//                mylikes.sortByDescending { it.second }
//
//                println(mylikes)
//                // Menampilkan data
//                for (like in mylikes) {
//                    idPostThatILike.add(like.second.toString())
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Jika terjadi error saat membaca database
//                println("Error: ${databaseError.message}")
//            }
//        })
//
//    }
//    open fun myLikes() {
//        var firebaseUser = FirebaseAuth.getInstance().currentUser
//        var databaseReference = FirebaseDatabase.getInstance().reference
//            .child("Likes")
//            .child(firebaseUser!!.uid)
//            .orderByValue().limitToLast(10)
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Mendapatkan nilai terbaru dari database
//                for (snapshot in dataSnapshot.children) {
//                    val key = snapshot.key.toString()
//                    key.let { mylikes.add(it) }
//                }
//                println(mylikes.toString())
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Jika terjadi error saat membaca database
//                println("Error: ${databaseError.message}")
//            }
//        })
//
//    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val targetValue = likeList.toList()[position].first
        var foundIndex = 0

        for (index in postList.indices) {
            if (postList[index].postid.toString().equals(targetValue)) {
                foundIndex = index
                println("Masuk")
                break
            }
        }
        val currentitem = postList[foundIndex]
        //Meletakan Photo Profile
        //if(idPostThatILike.contains(currentitem.postid)) {
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
                        .placeholder(DRAWABLE.homebliss)
                        .error(DRAWABLE.homebliss)
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
//        }else {
//            holder.cardHolder.visibility = View.GONE
//            holder.cardHolder.layoutParams = RecyclerView.LayoutParams(0, 0)
//        }
        holder.discussionBtn.setOnClickListener {
            var postToDiscuss=currentitem
            val intent = Intent(parent.context, DiscussionActivity::class.java)
            intent.putExtra("postToDiscuss", currentitem)
            parent.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return likeList.size
    }

    fun updatePostList(postList : List<Post>){
        this.postList.clear()
        this.postList.addAll(postList)
        notifyDataSetChanged()
    }
    fun updateLikeList(likeList: Map<String, Long>){
        this.likeList.clear()
        this.likeList.putAll(likeList.toList().sortedBy { (_, value) -> value })
        notifyDataSetChanged()
    }
    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var cardHolder : CardView = itemView.findViewById(ID.cardholder)
        var profileImage : CircleImageView = itemView.findViewById(ID.user_profile)
        var postImage : ImageView = itemView.findViewById(ID.post_image_home)
        var likeBtn : ImageView = itemView.findViewById(ID.post_image_like_btn)
        var judul : TextView = itemView.findViewById(ID.judul)
        var discussionBtn : ImageView = itemView.findViewById(ID.post_image_discuss_btn)
        var name1 : TextView = itemView.findViewById(ID.user_name_search)
        var caption : TextView = itemView.findViewById(ID.description)

    }
}