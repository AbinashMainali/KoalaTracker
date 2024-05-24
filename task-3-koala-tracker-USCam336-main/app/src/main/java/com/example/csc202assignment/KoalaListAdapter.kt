package com.example.csc202assignment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.csc202assignment.databinding.ListItemKoalaBinding
import java.util.*


class KoalaHolder(
        private val binding: ListItemKoalaBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(koala: Koala, onKoalaClicked: (koalaId: UUID) -> Unit){
            binding.koalaTitle.text = koala.title
            binding.koalaPlace.text = koala.place
            binding.koalaGpsLocation.text=String.format(
                "Lat: %s, Lon: %s",
                koala.lat,
                koala.lon
            )
            binding.koalaDate.text = koala.date.toString()

            binding.root.setOnClickListener {
               onKoalaClicked(koala.id)

            }
        }
    }


class KoalaListAdapter(
    private val kolas: List<Koala>,
    private val onKoalaClicked: (koalaId: UUID) -> Unit
    ) : RecyclerView.Adapter<KoalaHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) : KoalaHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemKoalaBinding.inflate(inflater, parent, false)
            return KoalaHolder(binding)
        }

        override fun onBindViewHolder(holder: KoalaHolder, position: Int) {
            val koala = kolas[position]
            holder.bind(koala, onKoalaClicked)
        }

        override fun getItemCount() = kolas.size
    }

