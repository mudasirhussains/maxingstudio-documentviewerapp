package com.mudassir.documentviewer.search

import android.os.Handler
import android.os.Looper
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
import java.util.*
import kotlin.collections.ArrayList

class SearchActivityAdapter(
    private var mFileList: ArrayList<DocNoteModel>,
    private val notesListener: ChipItemListeners
) :
    RecyclerView.Adapter<SearchActivityAdapter.NotesViewHolder>() {
    private var timer: Timer? = null
    private val notesSource: ArrayList<DocNoteModel>
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
            notesListener.onSearchItemClicked(mFileList[holder.adapterPosition])
        }

//        if (notes[holder.adapterPosition].favorite) {
//            holder.imageFav.setImageResource(R.drawable.ic_filled_fav)
//        } else {
//            holder.imageFav.setImageResource(R.drawable.ic_unfilled_fav)
//        }
//
//        holder.imgDetailFav.setOnClickListener {
//            if (notes[position].favorite) {
//                //notes[position].favorite = false
//                holder.imageFav.setImageResource(R.drawable.ic__star_unfav)
//                notesListener.onNoteFavorite(notes[holder.adapterPosition], holder.adapterPosition)
//            } else {
//                //notes[position].favorite = true
//                holder.imgDetailFav.setImageResource(R.drawable.ic_star_fav)
//                notesListener.onNoteFavorite(notes[holder.adapterPosition], holder.adapterPosition)
//            }
//        }
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
            if (!note.isNullOrEmpty()){
                setThumbnails(note)
                txtDetailTitle.text = note[adapterPosition].title
            }


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

    fun searchNotes(searchKeyword: String) {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                mFileList = if (searchKeyword.trim { it <= ' ' }.isEmpty()) {
                    notesSource
                } else {
                    val temp = ArrayList<DocNoteModel>()
                    for (note in notesSource) {
                        if (note.title.toLowerCase()
                                .contains(searchKeyword.lowercase(Locale.getDefault()))
//                            || note.subtitle.toLowerCase()
//                                .contains(searchKeyword.lowercase(Locale.getDefault()))
//                            || note.noteText.toLowerCase()
//                                .contains(searchKeyword.lowercase(Locale.getDefault()))
                        ) {
                            temp.add(note)
                        }
                    }
                    temp
                }
                Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
            }
        }, 500)
    }
    fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }

    init {
        notesSource = mFileList
    }
}
