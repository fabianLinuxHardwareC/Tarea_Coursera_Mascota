package com.example.ddd
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity2 : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MascotaAdapter

    private val seleccionarFoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            val nuevaMascota = Mascota(
                "Nueva mascota",
                0,
                0,
                uri
            )

            DatosMascotas.mascotas.add(0, nuevaMascota)

            adapter.notifyItemInserted(0)

            recycler.scrollToPosition(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla2)


        recycler = findViewById(R.id.vistaReciclada)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = MascotaAdapter(DatosMascotas.mascotas)

        recycler.adapter = adapter



        val barra = findViewById<MaterialToolbar>(R.id.barraNavegador)


        val opcionFavoritos = barra.menu.findItem(R.id.action_favoritos)

        opcionFavoritos.actionView?.setOnClickListener {

            val intent = Intent(
                this,
                FavoritosActivity::class.java
            )

            startActivity(intent)
        }


        val botonSubir = findViewById<FloatingActionButton>(
            R.id.botonFlotante
        )

        botonSubir.setOnClickListener {

            seleccionarFoto.launch("image/*")

        }

    }
}