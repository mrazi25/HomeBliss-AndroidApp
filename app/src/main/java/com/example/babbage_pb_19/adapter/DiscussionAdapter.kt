package com.example.babbage_pb_19.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.data.Discussion
import com.example.babbage_pb_19.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class DiscussionAdapter() : RecyclerView.Adapter<DiscussionAdapter.MyViewHolder>() {

    private val discussionList = ArrayList<Discussion>()
    private lateinit var parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.discussion_item,
            parent, false
        )
        this.parent = parent
        return MyViewHolder(itemView)

    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = discussionList[position]


        val userRef = FirebaseDatabase.getInstance().reference.child("Users")

        holder.comment_text.text = currentitem.discussion.toString()
        userRef.child(currentitem.commenter.toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    holder.commenter.text = it.child("name").value.toString()
                }
            }
            .addOnFailureListener {
                Toast.makeText(parent.context, "Error Occurred", Toast.LENGTH_SHORT)
                    .show()
                println("The Discussion data cannot be retrieve")
            }

        Picasso.get()
            .load(currentitem.img)
            .placeholder(R.drawable.homebliss)
            .error(R.drawable.homebliss)
            .into(holder.profile_pict)
    }

    override fun getItemCount(): Int {
        return discussionList.size
    }
    fun updateDiscussionList(discussionList: List<Discussion>) {

        this.discussionList.clear()
        this.discussionList.addAll(discussionList)
        notifyDataSetChanged()

    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_pict: CircleImageView = itemView.findViewById(R.id.profile_picture)
        var commenter: TextView = itemView.findViewById(R.id.commenter_name)
        var comment_text: TextView = itemView.findViewById(R.id.comment_text)
    }
}