package com.example.ddd

import android.net.Uri

data class Mascota(

    val nombre: String,

    val imagen: Int,

    var rating: Int,

    val fotoUri: Uri? = null

)