package com.example.ddd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class MascotaAdapter(
    private val mascotas: List<Mascota>
) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {


    class MascotaViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imagen = itemView.findViewById<ImageView>(
            R.id.imageView
        )

        val nombre = itemView.findViewById<TextView>(
            R.id.textView
        )

        val rating = itemView.findViewById<TextView>(
            R.id.textoRating
        )

        val botonHueso = itemView.findViewById<TextView>(
            R.id.botonHueso
        )

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MascotaViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_mascota,
                parent,
                false
            )

        return MascotaViewHolder(vista)
    }


    override fun onBindViewHolder(
        holder: MascotaViewHolder,
        position: Int
    ) {

        val mascota = mascotas[position]



        if (mascota.fotoUri != null) {

            holder.imagen.setImageURI(mascota.fotoUri)

        } else {

            holder.imagen.setImageResource(mascota.imagen)

        }



        holder.nombre.text = mascota.nombre



        holder.rating.text = mascota.rating.toString()



        holder.botonHueso.setOnClickListener {

            mascota.rating++

            holder.rating.text = mascota.rating.toString()

        }

    }


    override fun getItemCount(): Int {

        return mascotas.size

    }
}