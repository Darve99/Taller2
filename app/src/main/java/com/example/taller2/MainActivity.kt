package com.example.taller2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnMap = findViewById<Button>(R.id.btnMap)
        val btnContacts = findViewById<Button>(R.id.btnContacts)

        btnCamera.setOnClickListener {
            // Agrega aquí la lógica para abrir la cámara
        }

        btnMap.setOnClickListener {
            // Agrega aquí la lógica para abrir el mapa
        }

        btnContacts.setOnClickListener {
            val intent = Intent(this, ContactosActivity::class.java)
            startActivity(intent)
        }
    }
}
