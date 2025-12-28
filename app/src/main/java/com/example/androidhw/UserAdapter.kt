package com.example.androidhw

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class UserAdapter(context: Context, users: List<UserModel>) :
    ArrayAdapter<UserModel>(context, R.layout.item_user, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_user, parent, false)

        val holder = (view.tag as? ViewHolder) ?: ViewHolder(view).also { view.tag = it }
        val user = getItem(position)
        if (user != null) {
            holder.avatar.setImageResource(user.avatarResId)
            holder.name.text = user.fullName()
            holder.age.text = context.getString(R.string.age_format, user.age)
            holder.location.text = context.getString(R.string.location_format, user.country, user.city)
        }
        return view
    }

    private class ViewHolder(view: View) {
        val avatar: ImageView = view.findViewById(R.id.image_avatar)
        val name: TextView = view.findViewById(R.id.text_name)
        val age: TextView = view.findViewById(R.id.text_age)
        val location: TextView = view.findViewById(R.id.text_location)
    }
}
