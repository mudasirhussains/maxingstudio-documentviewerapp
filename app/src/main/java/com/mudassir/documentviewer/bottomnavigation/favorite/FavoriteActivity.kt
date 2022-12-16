package com.mudassir.documentviewer.bottomnavigation.favorite

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.mudassir.documentviewer.BuildConfig
import com.mudassir.documentviewer.R
import com.mudassir.documentviewer.databinding.ActivityFavoriteBinding
import com.mudassir.documentviewer.details.CommonDetailListeners
import com.mudassir.documentviewer.room.database.DatabaseBuilder
import com.mudassir.documentviewer.room.entity.DocNoteModel
import java.io.File

class FavoriteActivity : AppCompatActivity(), FavoriteListeners {
    private lateinit var binding : ActivityFavoriteBinding
    private var notesAdapter: FavoriteAdapter? = null
    private var docList: ArrayList<DocNoteModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        val adRequest = AdRequest.Builder().build()
        binding.adViewDialog.loadAd(adRequest)
        setCallBacks()
        getData()
        setRecyclerView()
    }

    private fun setBinding() {
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getData() {
        docList = DatabaseBuilder.getInstance(applicationContext).dao().getAllFavoriteNotes() as ArrayList<DocNoteModel>
    }

    private fun setCallBacks() {
        findViewById<ImageView>(R.id.imgGoBackFav).setOnClickListener {
            finish()
        }
    }

    private fun setRecyclerView() {
        if (docList.isEmpty()) {
            binding.layoutNoNotes.visibility = View.VISIBLE
        } else {
            binding.layoutNoNotes.visibility = View.GONE
            binding.notesRecyclerViewFav.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            notesAdapter = FavoriteAdapter(docList as ArrayList<DocNoteModel>, this)
            binding.notesRecyclerViewFav.adapter = notesAdapter
        }

    }
    override fun onFavoriteClickedForUnFav(id: Int, position: Int) {
        DatabaseBuilder.getInstance(this).dao().updateForFavorite(false, id)
        if (position != 0){
            if (docList.isNotEmpty()) {
                docList.removeAt(position)
                notesAdapter!!.notifyItemRemoved(position)
            } else {
                finish()
            }
        }else{
            finish()
        }

    }

    override fun onFavDetailItemClicked(docModel: DocNoteModel?) {
        if (docModel != null) {
            openFileUri(docModel)
        }
    }
    private fun openFileUri(file: DocNoteModel) {

        var fileMaker = File(file.absolutePath)
        val path: Uri = FileProvider.getUriForFile(
            this@FavoriteActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            fileMaker
        )
        //Uri path = Uri.fromFile(file);
        val pdfOpenintent = Intent(Intent.ACTION_VIEW)
        pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenintent.setDataAndType(path, "application/*")
        try {
            startActivity(pdfOpenintent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun onSendFile(docModel: DocNoteModel) {
        sendUsingURI(docModel)
    }

    private fun sendUsingURI(file: DocNoteModel){
        var fileMaker = File(file.absolutePath)
        val uri: Uri = FileProvider.getUriForFile(
            this@FavoriteActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            fileMaker
        )
        val intent = Intent()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.action = Intent.ACTION_SEND
        intent.type = "application/pdf"

        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        try {
            startActivity(Intent.createChooser(intent, "Share Via"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(applicationContext, "No Sharing App Found", Toast.LENGTH_SHORT).show()
        }
    }
}