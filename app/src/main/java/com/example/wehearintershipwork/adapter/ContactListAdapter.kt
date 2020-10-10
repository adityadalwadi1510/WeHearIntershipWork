package com.example.wehearintershipwork.adapter

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.model.ContactUser
import kotlinx.android.synthetic.main.contact_item.view.*

class ContactListAdapter(val items:List<ContactUser>) :RecyclerView.Adapter<ContactListAdapter.ContactListAdapterViewHolder>(){




    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactListAdapterViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.contact_item,parent,false)
        return ContactListAdapterViewHolder(v)
    }

    override fun onBindViewHolder(holder: ContactListAdapterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }



    class ContactListAdapterViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item:ContactUser)= with(itemView){
            Name_contact.text=item.name
            phone_contact.text=item.number
        }

    }
}