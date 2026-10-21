package com.example.ddd

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla1)

        val botonIniciar = findViewById<Button>(R.id.botonInicio)

        botonIniciar.setOnClickListener {

            val intent = Intent(this, MainActivity2::class.java)

            startActivity(intent)

        }
    }
}