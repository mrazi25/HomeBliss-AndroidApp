package com.example.babbage_pb_19.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.activity.DiscussionActivity
import com.example.babbage_pb_19.data.Post
import com.example.babbage_pb_19.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.logging.Handler

//class PostAdapter (private val mContext: Context, private val mPost: List<Post>) :RecyclerView.Adapter<PostAdapter.ViewHolder>(){
//
//    private var firebaseUser: FirebaseUser? = null
//
//    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
//        var profileImage : CircleImageView
//        lateinit var postImage : ImageView
//        lateinit var likeBtn : ImageView
//        lateinit var discussionBtn : ImageView
//        lateinit var name1 : TextView
//        lateinit var name2 : TextView
//        lateinit var likeNumb : TextView
//        lateinit var caption : TextView
//        lateinit var discussionText : TextView
//
//        init {
//            profileImage = itemView.findViewById(R.id.user_profile)
//            postImage = itemView.findViewById(R.id.post_image_home)
//            likeBtn = itemView.findViewById(R.id.post_image_like_btn)
//            discussionBtn = itemView.findViewById(R.id.post_image_discuss_btn)
//            name1 = itemView.findViewById(R.id.user_name_search)
//            name2 = itemView.findViewById(R.id.publisher)
//            likeNumb = itemView.findViewById(R.id.likes)
//            caption = itemView.findViewById(R.id.description)
//            discussionText = itemView.findViewById(R.id.comments)
//
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return mPost.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        firebaseUser = FirebaseAuth.getInstance().currentUser
//
//        val post = mPost[position]
//
//        Picasso.get().load(post.image).into(holder.postImage)
//
//        posterInfo(holder.profileImage, holder.name1, holder.postImage, post.postId)
//    }
//
//    private fun posterInfo(profileImage: CircleImageView, name1: TextView, postImage: ImageView, postId: String) {
//        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(postId)
//        usersRef.addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()){
//                    val user = snapshot.getValue<User>(User::class.java)
//
//                    Picasso.get().load(user!!.image).placeholder(R.drawable.ic_image_teal).into(profileImage)
//                    name1.text = user.name
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
//}

class PostAdapter : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private val postList = ArrayList<Post>()
    private var firebaseUser = FirebaseAuth.getInstance().currentUser
    private val mylikes : MutableList<String> = mutableListOf()
    private lateinit var parent: ViewGroup
    var postToDiscuss: Post? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.post_item,
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


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = postList[position]

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
        // Tampilkan loading
        val progressBar = ProgressBar(parent.context)
        progressBar.visibility = View.VISIBLE

        // Buat objek Handler
        val handler = android.os.Handler()

        // Tentukan penundaan (delay) dalam milidetik (misalnya, 2000 ms = 2 detik)
        val delayMillis = 2000L

        // Post penundaan (delay) pada Handler

        holder.discussionBtn.setOnClickListener {
            handler.postDelayed({
                // Kode yang ingin dijalankan setelah penundaan (delay) selesai
                postToDiscuss=currentitem
                // Sembunyikan loading
                val intent = Intent(parent.context, DiscussionActivity::class.java)
                intent.putExtra("postToDiscuss", currentitem)

                // Sembunyikan loading
                progressBar.visibility = View.GONE

                parent.context.startActivity(intent)
            }, delayMillis)
        }

    }

    private var data: List<Post> = emptyList()

    // Metode setter untuk mengatur data
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

        var profileImage : CircleImageView = itemView.findViewById(R.id.user_profile)
        var postImage : ImageView = itemView.findViewById(R.id.post_image_home)
        var likeBtn : ImageView = itemView.findViewById(R.id.post_image_like_btn)
        var judul : TextView = itemView.findViewById(R.id.judul)
        var discussionBtn : ImageView = itemView.findViewById(R.id.post_image_discuss_btn)
        var name1 : TextView = itemView.findViewById(R.id.user_name_search)
        var caption : TextView = itemView.findViewById(R.id.description)
        
    }

}
