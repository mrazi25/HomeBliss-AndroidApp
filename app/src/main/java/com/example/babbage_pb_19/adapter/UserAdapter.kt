package com.example.babbage_pb_19.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.data.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private var mContext:Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean=false) :RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.fragment_profile, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.nameTV.text = user.name.toString()
        holder.emailTV.text = user.email.toString()
        Picasso.get().load(user.image.toString()).placeholder(R.drawable.profile).into(holder.imageCIV)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTV: TextView = itemView.findViewById(R.id.etName)
        var emailTV: TextView = itemView.findViewById(R.id.etEmail)
        var imageCIV: CircleImageView = itemView.findViewById(R.id.ivProfile)
    }
}