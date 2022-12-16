package com.mudassir.documentviewer.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.room.entity.DocNoteModel
import java.io.File

class DetailCommonAdapter(
    private var mFileList: ArrayList<DocNoteModel>,
    private val notesListener: CommonDetailListeners
) :
    RecyclerView.Adapter<DetailCommonAdapter.NotesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.listitem_common,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setNote(mFileList)

        holder.lnCommon.setOnClickListener {
            notesListener.onCommonDetailItemClicked(mFileList[holder.adapterPosition])
        }


        holder.imgDetailMenu.setOnClickListener {
            notesListener.onSendFile(mFileList[holder.adapterPosition])
        }

        if (mFileList[holder.adapterPosition].favorite) {
            holder.imgDetailFav.setImageResource(R.drawable.ic_star_fav)
        } else {
            holder.imgDetailFav.setImageResource(R.drawable.ic__star_unfav)
        }

        holder.imgDetailFav.setOnClickListener {
            if (mFileList[position].favorite) {
                mFileList[position].favorite = false
                holder.imgDetailFav.setImageResource(R.drawable.ic__star_unfav)
                notesListener.onDocFavorite(mFileList[holder.adapterPosition], holder.adapterPosition)
            } else {
                mFileList[position].favorite = true
                holder.imgDetailFav.setImageResource(R.drawable.ic_star_fav)
                notesListener.onDocFavorite(mFileList[holder.adapterPosition], holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mFileList.size > 0) {
            mFileList.size
        } else {
            1
        }
        //return chips.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgThumbnail: ImageView
        var lnCommon: LinearLayoutCompat
        var txtDetailTitle: TextView
        var txtDetailTime: TextView
        var txtDetailSize: TextView
        var imgDetailFav: ImageView
        var imgDetailMenu: ImageView

        fun setNote(note: ArrayList<DocNoteModel>) {
            setThumbnails(note)
            txtDetailTitle.text = note[adapterPosition].title

        }


        init {
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail)
            lnCommon = itemView.findViewById(R.id.lnCommon)
            txtDetailTitle = itemView.findViewById(R.id.txtDetailTitle)
            txtDetailTime = itemView.findViewById(R.id.txtDetailTime)
            txtDetailSize = itemView.findViewById(R.id.txtDetailSize)
            imgDetailFav = itemView.findViewById(R.id.imgDetailFav)
            imgDetailMenu = itemView.findViewById(R.id.imgDetailMenu)
        }

        private fun setThumbnails(note: ArrayList<DocNoteModel>) {
            if (note[adapterPosition].title.endsWith(".pdf")) {
                imgThumbnail.setImageResource(R.drawable.ic_doc_pdf)
            } else if (note[adapterPosition].title.endsWith(".docx")) {
                imgThumbnail.setImageResource(R.drawable.ic_doc_doc)
            } else if (note[adapterPosition].title.endsWith(".ppt")) {
                imgThumbnail.setImageResource(R.drawable.ic_doc_ppt)
            } else if (note[adapterPosition].title.endsWith(".txt")) {
                imgThumbnail.setImageResource(R.drawable.ic_doc_txt)
            } else if (note[adapterPosition].title.endsWith(".xlsx")) {
                imgThumbnail.setImageResource(R.drawable.ic_doc_xls)
            } else {
                imgThumbnail.setImageResource(R.drawable.ic_doc_txt)
            }
        }
    }
}
