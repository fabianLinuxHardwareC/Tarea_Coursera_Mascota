package com.example.ddd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla_favoritos)

        val barra = findViewById<MaterialToolbar>(
            R.id.barraFavoritos
        )

        barra.setNavigationOnClickListener {
            finish()
        }

        val recycler = findViewById<RecyclerView>(
            R.id.recyclerFavoritos
        )

        recycler.layoutManager = LinearLayoutManager(this)

        val mascotasFavoritas = mutableListOf<Mascota>()

        val adapter = MascotaAdapter(mascotasFavoritas)

        recycler.adapter = adapter

        lifecycleScope.launch {

            val lista = withContext(Dispatchers.IO) {

                val baseDatos = BaseDatosMascotas(applicationContext)

                try {

                    baseDatos.obtenerUltimasCinco()

                } finally {

                    baseDatos.close()
                }
            }

            mascotasFavoritas.clear()

            mascotasFavoritas.addAll(lista)

            adapter.notifyDataSetChanged()
        }
    }
}