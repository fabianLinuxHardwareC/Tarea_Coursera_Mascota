package com.example.ddd

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class BaseDatosMascotas(context: Context) :
    SQLiteOpenHelper(context, "Petagram.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE catalogo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                imagen INTEGER NOT NULL,
                rating INTEGER NOT NULL,
                fotoUri TEXT,
                ultimaValoracion INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE mascota (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                imagen INTEGER NOT NULL,
                rating INTEGER NOT NULL,
                fotoUri TEXT,
                ultimaValoracion INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        if (oldVersion < 2) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS catalogo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    imagen INTEGER NOT NULL,
                    rating INTEGER NOT NULL,
                    fotoUri TEXT,
                    ultimaValoracion INTEGER
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT OR IGNORE INTO catalogo
                (id, nombre, imagen, rating, fotoUri, ultimaValoracion)
                SELECT id, nombre, imagen, rating, fotoUri, ultimaValoracion
                FROM mascota
                """.trimIndent()
            )

            db.execSQL("DELETE FROM mascota")
        }
    }

    private fun crearValores(mascota: Mascota): ContentValues {

        val datos = ContentValues()

        datos.put("nombre", mascota.nombre)
        datos.put("imagen", mascota.imagen)
        datos.put("rating", mascota.rating)
        datos.put("fotoUri", mascota.fotoUri?.toString())
        datos.put("ultimaValoracion", mascota.ultimaValoracion)

        return datos
    }

    fun guardarMascota(mascota: Mascota): Long {

        val datos = crearValores(mascota)

        val id = writableDatabase.insert(
            "catalogo",
            null,
            datos
        )

        if (id != -1L) {
            mascota.id = id
        }

        return id
    }

    fun actualizarValoracion(mascota: Mascota): Int {

        val db = writableDatabase

        val fecha = System.currentTimeMillis()

        val datos = ContentValues()

        datos.put("rating", mascota.rating)
        datos.put("ultimaValoracion", fecha)

        db.beginTransaction()

        try {

            val resultado = db.update(
                "catalogo",
                datos,
                "id = ?",
                arrayOf(mascota.id.toString())
            )

            if (resultado > 0) {

                val historial = crearValores(mascota)

                historial.put("id", mascota.id)
                historial.put("ultimaValoracion", fecha)

                val guardado = db.insertWithOnConflict(
                    "mascota",
                    null,
                    historial,
                    SQLiteDatabase.CONFLICT_REPLACE
                )

                if (guardado == -1L) {
                    return 0
                }

                db.execSQL(
                    """
                    DELETE FROM mascota
                    WHERE id NOT IN (
                        SELECT id FROM mascota
                        ORDER BY ultimaValoracion DESC, id DESC
                        LIMIT 5
                    )
                    """.trimIndent()
                )

                mascota.ultimaValoracion = fecha
            }

            db.setTransactionSuccessful()

            return resultado

        } finally {

            db.endTransaction()
        }
    }

    fun obtenerMascotas(): MutableList<Mascota> {

        return consultarMascotas(
            "SELECT * FROM catalogo ORDER BY id ASC"
        )
    }

    fun obtenerUltimasCinco(): MutableList<Mascota> {

        return consultarMascotas(
            """
            SELECT * FROM mascota
            ORDER BY ultimaValoracion DESC, id DESC
            LIMIT 5
            """.trimIndent()
        )
    }

    private fun consultarMascotas(sql: String): MutableList<Mascota> {

        val lista = mutableListOf<Mascota>()

        val cursor = readableDatabase.rawQuery(sql, null)

        cursor.use {

            while (it.moveToNext()) {

                val id = it.getLong(
                    it.getColumnIndexOrThrow("id")
                )

                val nombre = it.getString(
                    it.getColumnIndexOrThrow("nombre")
                )

                val imagen = it.getInt(
                    it.getColumnIndexOrThrow("imagen")
                )

                val rating = it.getInt(
                    it.getColumnIndexOrThrow("rating")
                )

                val indiceFoto = it.getColumnIndexOrThrow(
                    "fotoUri"
                )

                val foto = if (it.isNull(indiceFoto)) {
                    null
                } else {
                    Uri.parse(it.getString(indiceFoto))
                }

                val indiceFecha = it.getColumnIndexOrThrow(
                    "ultimaValoracion"
                )

                val fecha = if (it.isNull(indiceFecha)) {
                    null
                } else {
                    it.getLong(indiceFecha)
                }

                lista.add(
                    Mascota(
                        nombre = nombre,
                        imagen = imagen,
                        rating = rating,
                        fotoUri = foto,
                        id = id,
                        ultimaValoracion = fecha
                    )
                )
            }
        }

        return lista
    }
}