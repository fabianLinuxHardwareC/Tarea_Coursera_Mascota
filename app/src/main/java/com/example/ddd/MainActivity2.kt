package com.example.ddd

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity2 : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MascotaAdapter
    private lateinit var baseDatos: BaseDatosMascotas

    private val mascotas = mutableListOf<Mascota>()

    private val seleccionarFoto = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {

            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val nuevaMascota = Mascota(
                "Nueva mascota",
                0,
                0,
                uri
            )

            lifecycleScope.launch {

                val id = withContext(Dispatchers.IO) {
                    baseDatos.guardarMascota(nuevaMascota)
                }

                if (id != -1L) {

                    mascotas.add(nuevaMascota)

                    adapter.notifyItemInserted(mascotas.lastIndex)

                    recycler.scrollToPosition(mascotas.lastIndex)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pantalla2)

        baseDatos = BaseDatosMascotas(this)

        recycler = findViewById(R.id.vistaReciclada)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = MascotaAdapter(mascotas) { mascota ->

            lifecycleScope.launch {

                withContext(Dispatchers.IO) {

                    baseDatos.actualizarValoracion(mascota)

                }
            }
        }

        recycler.adapter = adapter

        val botonSubir = findViewById<FloatingActionButton>(
            R.id.botonFlotante
        )

        botonSubir.isEnabled = false

        lifecycleScope.launch {

            val lista = withContext(Dispatchers.IO) {

                var datos = baseDatos.obtenerMascotas()

                if (datos.isEmpty()) {

                    for (mascota in DatosMascotas.mascotas) {
                        baseDatos.guardarMascota(mascota)
                    }

                    datos = baseDatos.obtenerMascotas()
                }

                datos
            }

            mascotas.clear()

            mascotas.addAll(lista)

            adapter.notifyDataSetChanged()

            botonSubir.isEnabled = true
        }

        val barra = findViewById<MaterialToolbar>(
            R.id.barraNavegador
        )

        val opcionFavoritos = barra.menu.findItem(
            R.id.action_favoritos
        )

        opcionFavoritos.actionView?.setOnClickListener {

            val intent = Intent(
                this,
                FavoritosActivity::class.java
            )

            startActivity(intent)
        }

        botonSubir.setOnClickListener {

            seleccionarFoto.launch(arrayOf("image/*"))
        }
    }

    override fun onDestroy() {
        baseDatos.close()
        super.onDestroy()
    }
}