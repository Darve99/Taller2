package com.example.taller2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactListAdapter(private val contactList: List<String>) :
    RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_list_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactName = contactList[position]
        holder.contactNameTextView.text = "${position + 1}.      $contactName"
        holder.contactIconImageView.setImageResource(R.drawable.ic_contact) // Reemplaza ic_contact con el nombre de tu icono de contactos
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameTextView: TextView = itemView.findViewById(R.id.contactNameTextView)
        val contactIconImageView: ImageView = itemView.findViewById(R.id.contactIconImageView)
    }
}
