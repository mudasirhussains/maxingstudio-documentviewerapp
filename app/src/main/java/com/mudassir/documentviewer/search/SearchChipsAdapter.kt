package com.mudassir.documentviewer.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mudassir.documentviewer.R

class SearchChipsAdapter(private var chips: List<String>, private val notesListener: ChipItemListeners) :
    RecyclerView.Adapter<SearchChipsAdapter.NotesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.listitem_chips,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setNote(chips[position])


        holder.chipItemCard.setOnClickListener {
            notesListener.onChipItemClicked(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return chips.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chipItemName: TextView
        var chipItemCard: LinearLayoutCompat
        fun setNote(note: String) {
            chipItemName.text = note
        }

        init {
            chipItemName = itemView.findViewById(R.id.chipItemName)
            chipItemCard = itemView.findViewById(R.id.chipItemCard)
        }
    }
}
