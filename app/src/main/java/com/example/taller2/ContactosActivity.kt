package com.example.taller2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        recyclerView = findViewById(R.id.recyclerViewContacts)

        // Verificar si se tiene el permiso READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar el permiso si no está concedido
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_CODE
            )
        } else {
            // Si el permiso está concedido, mostrar la lista de contactos
            displayContacts()
        }
    }

    private fun displayContacts() {
        // Acceder a los contactos utilizando el Content Provider
        val contacts = ArrayList<String>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.use {
            val contactNameColumnIndex =
                it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (it.moveToNext()) {
                if (contactNameColumnIndex >= 0) {
                    val contactName = it.getString(contactNameColumnIndex)
                    contacts.add(contactName)
                } else {
                    // Manejar el caso en el que la columna DISPLAY_NAME no existe
                    // Por ejemplo, puedes mostrar un valor predeterminado o realizar otra acción
                }
            }
        }

        // Mostrar la lista de contactos en un RecyclerView
        val adapter = ContactListAdapter(contacts)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // Manejar la respuesta de la solicitud de permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONTACTS_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, mostrar la lista de contactos
                    displayContacts()
                } else {
                    // Permiso denegado, manejar este caso según tus necesidades
                }
            }
        }
    }

    companion object {
        private const val CONTACTS_PERMISSION_CODE = 1
    }
}
