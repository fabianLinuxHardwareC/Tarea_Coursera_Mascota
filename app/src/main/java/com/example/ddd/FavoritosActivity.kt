package com.example.ddd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class FavoritosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla_favoritos)

        val barra = findViewById<MaterialToolbar>(R.id.barraFavoritos)

        barra.setNavigationOnClickListener {
            finish()
        }

        val mascotasFavoritas = listOf(

            Mascota("Ronny", R.drawable.perro, 3),

            Mascota("Catty", R.drawable.gato, 5),

            Mascota("Rocky", R.drawable.armadillo, 4),

            Mascota("Luna", R.drawable.lagarto, 5),

            Mascota("Max", R.drawable.perro, 2)

        )

        // CONFIGURAR RECYCLERVIEW
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)

        recycler.layoutManager = LinearLayoutManager(this)

        recycler.adapter = MascotaAdapter(mascotasFavoritas)
    }
}
