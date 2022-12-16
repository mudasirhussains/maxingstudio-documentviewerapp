package com.mudassir.documentviewer.bottomnavigation.recent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.bottomnavigation.favorite.FavoriteListeners
import com.mudassir.documentviewer.details.CommonDetailListeners
import com.mudassir.documentviewer.room.entity.DocNoteModel
import java.util.*

class RecentAdapter(private var mFileList: ArrayList<DocNoteModel>, private val notesListener: FavoriteListeners) :
    RecyclerView.Adapter<RecentAdapter.NotesViewHolder>() {
    private val notesSource: List<DocNoteModel>
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
            notesListener.onFavDetailItemClicked(mFileList[holder.adapterPosition])
        }

        if (mFileList[holder.adapterPosition].favorite) {
            holder.imgDetailFav.setImageResource(R.drawable.ic_star_fav)
        } else {
            holder.imgDetailFav.setImageResource(R.drawable.ic__star_unfav)
        }


//        holder.imgDetailFav.setOnClickListener {
//            notesListener.onFavoriteClickedForUnFav(mFileList[holder.adapterPosition].id, holder.adapterPosition)
//        }
    }

    override fun getItemCount(): Int {
        return mFileList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgThumbnail: ImageView
        var lnCommon: LinearLayoutCompat
        var extraIcons: LinearLayoutCompat
        var txtDetailTitle: TextView
        var txtDetailTime: TextView
        var txtDetailSize: TextView
        var imgDetailFav: ImageView
        var imgDetailMenu: ImageView
        fun setNote(note: ArrayList<DocNoteModel>) {
            extraIcons.visibility = View.GONE
            if (!note.isNullOrEmpty()){
                setThumbnails(note)
                txtDetailTitle.text = note[adapterPosition].title
            }
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

        init {
            //TODO Replace findViewByID with View Binding
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail)
            extraIcons = itemView.findViewById(R.id.extraIcons)
            lnCommon = itemView.findViewById(R.id.lnCommon)
            txtDetailTitle = itemView.findViewById(R.id.txtDetailTitle)
            txtDetailTime = itemView.findViewById(R.id.txtDetailTime)
            txtDetailSize = itemView.findViewById(R.id.txtDetailSize)
            imgDetailFav = itemView.findViewById(R.id.imgDetailFav)
            imgDetailMenu = itemView.findViewById(R.id.imgDetailMenu)
        }
    }

    init {
        notesSource = mFileList
    }
}
